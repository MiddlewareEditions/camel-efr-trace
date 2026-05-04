# camel-efr-trace

A custom [Apache-Camel](https://camel.apache.org/) component to easily send traces to [Enterprise Flows Repository](https://enterpriseflowsrepository.com/).

## How to use

### 1. Kamelet configuration

You'll need to register the component as a Kamelet in your cluster. You can do this by applying the following manifest:

```bash
kubectl apply -f https://raw.githubusercontent.com/MiddlewareEditions/camel-efr-trace/k8s/efr-trace.kamelet.yaml
```

> [!TIP]
> This file contains a `$.metadata.namespace` value. Set it to `default` or to the namespace where you want to use the component.

### 2. Application configuration

In your application properties, add the following :

```properties
# EFR OIDC configuration
efr.hostname=https://${efr.environment}.enterpriseflowsrepository.com
efr.oidc.hostname=${efr.hostname}/auth
efr.oidc.realm=efr
efr.oidc.grant-type=password
efr.oidc.client-id=backend-tools
efr.oidc.client-secret=CLIENT_AUTH_SECRET
efr.oidc.username=CLIENT_AUTH_USERNAME
efr.oidc.password=CLIENT_AUTH_PASSWORD
efr.environment=CLIENT_ENVIRONMENT
efr.trace.environment=CLIENT_SUB_ENVIRONMENT

# EFR trace meta-data
route.datacenter=SOME_VALUE
route.id=test-route
route.name=${quarkus.application.name}
route.version=${quarkus.application.version}

# Classic business-key
trace.business=Client=clientId
```

### 3. Route usage

Here is an example using the XML DSL.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!-- camel-k: language=xml -->
<!-- camel-k: dependency=mvn:com.enterpriseflowsrepository:camel-efr-trace:0.0.1-r9 -->

<routes xmlns="http://camel.apache.org/schema/spring">
  <route id="test-route">
    <from uri="timer:test-route?fixedRate=true&amp;period=5000&amp;repeatCount=-1" />
    
    <!-- Examples of properties -->
    <setHeader name="some-header"><constant>some-value</constant></setHeader>
    <setProperty name="clientId"><constant>client_0001</constant></setProperty>
    
    <!-- Trace payload -->
    <setBody><constant>Actual payload.</constant></setBody>
    <setProperty name="description"><simple>ici ça décrit</simple></setProperty>
    <setProperty name="step"><simple>STEP DEMO</simple></setProperty>
    
    <!-- Usage -->
    <to uri="kamelet:efr-trace" /> <!-- info -->
    <to uri="kamelet:efr-trace?level=warning" />
    <to uri="kamelet:efr-trace?level=error" />
    <to uri="kamelet:efr-trace?level=success" />
  </route>
</routes>
```

> [!IMPORTANT]  
> The modeline at the top allows to select what version of the dependency should be used.  
> You can also add a `-d mvn:com.enterpriseflowsrepository:camel-efr-trace:<VERSION>` in the CLI argument if you prefer.

Using this kind of startup will allow you to deploy this route easily.

```bash
# THe same as in the kamelet !
NAMESPACE=kamel-2-9-0

kamel run test-route.xml -n $NAMESPACE \
    --property "file:example.properties" \
    --dev
```

