# apiman-cli: A CLI for apiman [![Build Status](https://travis-ci.org/apiman/apiman-cli.svg?branch=master)](https://travis-ci.org/apiman/apiman-cli)

Manage your [apiman](http://apiman.io) instances from the command line.

Script actions, such as adding APIs and gateways, or display information about a running _apiman_ environment.

## Example

Let's assume you have an _apiman_ server running on http://localhost:8080

Step 1: Create a new API:

    $ ./apiman manager api create \
            --name example \
            --endpoint http://example.com \
            --initialVersion 1.0 \
            --public \
            --orgName test

Step 2: Publish it:

    $ ./apiman manager api publish \
            --name example \
            --version 1.0 \
            --orgName test

You're done! Hit your new API at: [http://localhost:8080/apiman-gateway/test/example/1.0](http://localhost:8080/apiman-gateway/test/example/1.0)

## Management example

You can also manage your _apiman_ server.
    
Add a gateway:

    $ ./apiman manager gateway create \
            --name test-gw \
            --endpoint http://localhost:1234 \
            --username apimanager \
            --password "apiman123!" \
            --type REST

Add a plugin:

    $ ./apiman manager plugin add \
            --groupId io.apiman.plugins \
            --artifactId apiman-plugins-test-policy \
            --version 1.2.4.Final

You can do much more - see the [Usage](#usage) section.

## Declarative API management

Whilst running commands to control your _apiman_ environment from the CLI can be helpful, sometimes you need to keep your configuration in a file that you can check into your source control system.

For this, _apiman-cli_ has **Declarative Mode**.

Here's how it works:

### Step 1: Declare your API environment

Here's a simple YAML file (you can use JSON if you want):

    # simple.yml
    ---
    org:
      name: "test"
      description: "Test organisation"
      apis:
        - name: "example"
          description: "Example API"
          version: "1.0"
          published: true
          config:
            endpoint: "http://example.com"
            endpointType: "rest"
            public: true
            gateway: "TheGateway"
          policies:
            - name: "CachingPolicy"
              config:
                ttl: 60
          definition:
            file: "/home/user/swagger/example.json"
            type: "application/json"

### Step 2: Apply the environment declaration

    $ ./apiman manager apply -f simple.yml
    INFO Loaded declaration: examples/declarative/simple.yml
    INFO Adding org: test
    INFO Adding API: example
    INFO Configuring API: example
    INFO Setting definition for API: example
    INFO Adding policy 'CachingPolicy' to API: example
    INFO Publishing API: example
    INFO Applied declaration

The following things just happened:

1. an organisation named `test` was created,
2. an API named `example` was added with the endpoint `http://example.com`,
3. a swagger definition was uploaded for the API in json format
4. a caching policy was added to the API and configured with a TTL of 60 seconds and, finally,
5. the API was published to the gateway.
    
Declarations also allow you to add gateways, install plugins and more. See the `examples` directory.

## Using placeholder properties

You can also use placeholders in your declaration files. This helps you reuse declaration files across different environments. For example:

    endpoint: "${myApiEndpoint}"

...then pass them in when you run the _apply_ command:

    ./apiman manager apply -f simple.yml -P myApiEndpoint=http://example.com

Additionally, you can specify a properties files, containing key-value pairs, such as:

    ./apiman manager apply -f simple.yml --propertiesFile /path/to/placeholder.properties

## Shared policies and properties

To avoid repeating the same policy definitions, you can define them once in the _shared_ section of your declaration file,
then refer to them by name later.

For example, see the [shared-policies.yml](examples/declarative/shared-policies.yml) file.

The same goes for properties - you can define them in the _shared_ section and reuse them.

See the [shared-properties.yml](examples/declarative/shared-properties.yml) example file.

# Requirements

  * An instance of [apiman](http://apiman.io)
  * JDK 8
  * OS X, Windows, Linux

# Usage

The CLI lets you control both the apiman Manager and apiman Gateway components.
 
Typically, you will administer the Manager, then publish your changes to the Gateway (as per the UI flow).

## Manager commands 

    apiman manager plugin [args...]
    apiman manager org [args...]
    apiman manager api [args...]
    apiman manager gateway [args...]
    apiman manager apply [args...]
    
    --debug                    : Log at DEBUG level (default: false)
    --help (-h)                : Display usage only (default: false)
    --server (-s) VAL          : Management API server address (default:
                                 http://localhost:8080/apiman)
    --serverPassword (-sp) VAL : Management API server password (default:
                                 admin123!)
    --serverUsername (-su) VAL : Management API server username (default: apiman)
                      
### Manage Organisations
   
    apiman manager org show [args...]
    apiman manager org create [args...]
    
#### Show Org
    
    apiman manager org show [args...]
    
    --name (-n) VAL            : Name

#### Create Org
    
    apiman manager org create [args...]
    
    --description (-d) VAL     : Description
    --name (-n) VAL            : Name

### Manage Gateways
   
    apiman manager gateway test [args...]
    apiman manager gateway show [args...]
    apiman manager gateway create [args...]
    apiman manager gateway list [args...]

#### List Gateways
    
    apiman manager gateway list [args...]
    
#### Create Gateway
    
    apiman manager gateway create [args...]
    
    --description (-d) VAL     : Description
    --endpoint (-e) VAL        : Endpoint
    --name (-n) VAL            : Name
    --password (-p) VAL        : Password
    --type (-t) [REST | SOAP]  : type (default: REST)
    --username (-u) VAL        : Username

#### Show Gateway
    
    apiman manager gateway show [args...]
    
    --name (-n) VAL            : Name

#### Test Gateway
    
    apiman manager gateway test [args...]
    
    --description (-d) VAL     : Description
    --endpoint (-e) VAL        : Endpoint
    --name (-n) VAL            : Name
    --password (-p) VAL        : Password
    --type (-t) [REST | SOAP]  : type (default: REST)
    --username (-u) VAL        : Username

### Manage Plugins
   
    apiman manager plugin show [args...]
    apiman manager plugin add [args...]
    apiman manager plugin list [args...]

#### Show Plugin
   
    apiman manager plugin show [args...]
   
    --debug                    : Log at DEBUG level (default: false)
    --help (-h)                : Display usage only (default: false)
    --id (-i) VAL              : Plugin ID

#### Add Plugin
    
    apiman manager plugin add [args...]
    
    --artifactId (-a) VAL      : Artifact ID
    --classifier (-c) VAL      : Classifier
    --groupId (-g) VAL         : Group ID
    --version (-v) VAL         : Version

### List Plugins
    
    apiman manager plugin list [args...]
      
### Manage APIs
   
    apiman manager api create [args...]
    apiman manager api list [args...]
    apiman manager api publish [args...]

#### Create API
    
    apiman manager api create [args...]
    
    --endpoint (-e) VAL                 : Endpoint
    --endpointType (-t) VAL             : Endpoint type (default: rest)
    --gateway (-g) VAL                  : Gateway (default: TheGateway)
    --initialVersion (-v) VAL           : Initial version
    --name (-n) VAL                     : API name
    --orgName (-o) VAL                  : Organisation name
    --public (-p)                       : Public API
    --serverVersion (-sv) [v11x | v12x] : Management API server version (default:
                                          v11x)

#### List APIs
    
    apiman manager api list [args...]
    
    --orgName (-o) VAL                  : Organisation name
    --serverVersion (-sv) [v11x | v12x] : Management API server version (default:
                                          v11x)

#### Publish API
    
    apiman manager api publish [args...]
    
    --version (-v) VAL                  : API version
    --name (-n) VAL                     : API name
    --orgName (-o) VAL                  : Organisation name
    --serverVersion (-sv) [v11x | v12x] : Management API server version (default:
                                          v11x)

### Apply declaration

    apiman manager apply [args...]
    
     --declarationFile (-f) PATH : Declaration file
     -P VAL                      : Set property (key=value)

## Gateway commands

The following commands are available, when administering the Gateway directly:
    
    apiman gateway generate: Generate configurations
    apiman gateway apply: Apply Apiman Gateway declaration
    apiman gateway org: List Organizations
    apiman gateway api: Retire and list APIs
    apiman gateway client: Retire and list Clients
    apiman gateway status: View Gateway Status
    
    --debug: Log at DEBUG level
    --help, -h: Display usage only

# Recent changes and Roadmap

For recent changes see the [Changelog](CHANGELOG.md).

## Roadmap

* Support reading management API configuration from environment variables
* Better support for non-public APIs
* Support deletion
* Support for retiring published APIs
* Option to skip or fail for existing items in declarative mode
* Docs - split examples into separate file
* Docs - split detailed API usage into separate file
* Docs - simplify README examples

# Building

If you just want to run _apiman-cli_, use the _apiman_ or _apiman.bat_ (Windows) scripts in the root directory.

If you want to compile the JAR yourself, use:

    ./gradlew clean build
    
Note: for distribution, _apiman-cli_ is built as a 'fat JAR' (aka 'shadow JAR'). To do this yourself, run:

    ./gradlew clean shadowJar

...and look under the `build/libs` directory.

Importing into your favourite IDE is easy, as long as it supports Gradle projects.

## Tests
If you want to run unit tests, run:

    ./gradlew clean test

If you want to run integration tests, ensure you have an _apiman_ instance running on http://localhost:8080, then run:

    ./gradlew clean test -PintegrationTest
    
# Contributing

Pull requests are welcome.

# Author

Pete Cornish (outofcoffee@gmail.com)
