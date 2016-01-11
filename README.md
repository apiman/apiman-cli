# apiman-cli: A CLI for apiman

Manage your _apiman_ instances from the command line, script actions, such as adding services and gateways, or display information about a running _apiman_ instance. 

## Usage:

    apiman plugin [args...]
    apiman org [args...]
    apiman service [args...]
    apiman gateway [args...]
    
    --debug           : Log at DEBUG level (default: false)
    --help (-h)       : Display usage only (default: false)
    --server (-s) VAL : Management API server address (default:
                        http://localhost:8080/apiman)
                     
## Manage Organisations:
   
    apiman org show [args...]
    apiman org create [args...]

## Manage Gateways:
   
    apiman gateway test [args...]
    apiman gateway show [args...]
    apiman gateway create [args...]
    apiman gateway list [args...]
                        
## Manage Plugins:
   
    apiman plugin show [args...]
    apiman plugin create [args...]
    apiman plugin list [args...]
                        
## Manage Services:
   
    apiman service create [args...]

# TODO

* Externalise management API configuration
* Support publishing services
* Support non-public services
* Support deletion

# Contributing

Pull requests are welcome.

# Author

Pete Cornish (outofcoffee@gmail.com)