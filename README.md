# apiman-cli: A CLI for apiman [![Build Status](https://travis-ci.org/outofcoffee/apiman-cli.svg?branch=master)](https://travis-ci.org/outofcoffee/apiman-cli)

Manage your _apiman_ instances from the command line.

Script actions, such as adding APIs and gateways, or display information about a running _apiman_ instance.

## Example

Create a new API:

    $ ./apiman api create \
            --server http://localhost:8080/apiman \
            --name example \
            --endpoint http://example.com \
            --initialVersion 1.0 \
            --public \
            --orgName test
    
Add a gateway:

    $ ./apiman gateway create \
            --server http://localhost:8080/apiman \
            --name test-gw \
            --endpoint http://localhost:1234 \
            --username apimanager \
            --password "apiman123!" \
            --type REST

## Requirements

* JDK 8
* OS X, Windows, Linux

## Usage

    apiman plugin [args...]
    apiman org [args...]
    apiman api [args...]
    apiman gateway [args...]
    
    --debug                    : Log at DEBUG level (default: false)
    --help (-h)                : Display usage only (default: false)
    --server (-s) VAL          : Management API server address (default:
                                 http://localhost:8080/apiman)
    --serverPassword (-sp) VAL : Management API server password (default:
                                 admin123!)
    --serverUsername (-su) VAL : Management API server username (default: apiman)
                      
## Manage Organisations
   
    apiman org show [args...]
    apiman org create [args...]
    
### Show Org
    
    apiman org show [args...]
    
    --name (-n) VAL            : Name

### Create Org
    
    apiman org create [args...]
    
    --description (-d) VAL     : Description
    --name (-n) VAL            : Name

## Manage Gateways
   
    apiman gateway test [args...]
    apiman gateway show [args...]
    apiman gateway create [args...]
    apiman gateway list [args...]

### List Gateways
    
    apiman gateway list [args...]
    
### Create Gateway
    
    apiman gateway create [args...]
    
    --description (-d) VAL     : Description
    --endpoint (-e) VAL        : Endpoint
    --name (-n) VAL            : Name
    --password (-p) VAL        : Password
    --type (-t) [REST | SOAP]  : type (default: REST)
    --username (-u) VAL        : Username

### Show Gateway
    
    apiman gateway show [args...]
    
    --name (-n) VAL            : Name

### Test Gateway
    
    apiman gateway test [args...]
    
    --description (-d) VAL     : Description
    --endpoint (-e) VAL        : Endpoint
    --name (-n) VAL            : Name
    --password (-p) VAL        : Password
    --type (-t) [REST | SOAP]  : type (default: REST)
    --username (-u) VAL        : Username

## Manage Plugins
   
    apiman plugin show [args...]
    apiman plugin add [args...]
    apiman plugin list [args...]

### Show Plugin
   
    apiman plugin show [args...]
   
    --debug                    : Log at DEBUG level (default: false)
    --help (-h)                : Display usage only (default: false)
    --id (-i) VAL              : Plugin ID

### Add Plugin
    
    apiman plugin add [args...]
    
    --artifactId (-a) VAL      : Artifact ID
    --classifier (-c) VAL      : Classifier
    --groupId (-g) VAL         : Group ID
    --version (-v) VAL         : Version

### List Plugins
    
    apiman plugin list [args...]
      
## Manage APIs
   
    apiman api create [args...]
    apiman api list [args...]
    apiman api publish [args...]

### Create API
    
    apiman api create [args...]
    
    --endpoint (-e) VAL                 : Endpoint
    --endpointType (-t) VAL             : Endpoint type (default: rest)
    --gateway (-g) VAL                  : Gateway (default: TheGateway)
    --initialVersion (-v) VAL           : Initial version
    --name (-n) VAL                     : API name
    --orgName (-o) VAL                  : Organisation name
    --public (-p)                       : Public API
    --serverVersion (-sv) [v119 | v12x] : Management API server version (default:
                                          v119)

### List APIs
    
    apiman api list [args...]
    
    --orgName (-o) VAL                  : Organisation name
    --serverVersion (-sv) [v119 | v12x] : Management API server version (default:
                                          v119)

### Publish API
    
    apiman api publish [args...]
    
    --version (-v) VAL                  : API version
    --name (-n) VAL                     : API name
    --orgName (-o) VAL                  : Organisation name
    --serverVersion (-sv) [v119 | v12x] : Management API server version (default:
                                          v119)
     
# TODO

* Support reading management API configuration from environment variables
* Support adding policies to services
* Better support for non-public services
* Support deletion
* Declarative API environment definitions
  * Option to skip or fail for existing items
* Docs - split examples into separate file
* Docs - split detailed API usage into separate file
* Docs - simplify README examples

# Contributing

Pull requests are welcome.

# Author

Pete Cornish (outofcoffee@gmail.com)
