# K-car
### a factory for building and deploying Java microservices 

---

### what is this? 
K-car is the fastest, most flexible, way to create and deploy java microservices. It uses [Apache Camel](https://camel.apache.org/), a 'message oriented middleware' Java toolkit to empower teams to deliver complex business logic in a way that turbo charges both velocity AND quality. Units of business logic (*elements*), collections of business logic (*routes*), and the service platform on which routes are deployed (*chassis*) are all 100% encapsulated. This allows engineers to work independently, for every element in the assembly to have its own robust set of tests, and for elements to be re-used without cutting and pasting source from one project to another. 

The project is called __K-car__ in honor of [Chrysler's K platform](https://en.wikipedia.org/wiki/Chrysler_K_platform) from which the philosophy of building complex machinery from a collection of standardized, encapsulated, components is derived. 

### how does it work?
K-car is a factory for producing and deploying services written in Apache Camel. It leverages the ability of Apache Maven to create project templates called 'archetypes' to enable engineers to create 

### prerequisites

__stack prerequisites__
* maven 3
* java 11
* python3 (for the helper script `arch.py`)
* docker 

[SDK-man](https://sdkman.io/) is an excellent way to install maven and java. Python3 is almost certainly already installed on your workstation. The script that requires it does not require any special libraries. Docker can be installed from their [website](https://www.docker.com/products/docker-desktop). 

__knowledge prerequisites__
* apache camel [link](https://camel.apache.org/)
* Enterprise Integration Patterns [link](https://www.enterpriseintegrationpatterns.com/)

Working knowledge of Apache Camel is a must - though if you're a Java developer already you'll grok Camel very quickly. As for the EIPs, because Camel is a tool for implementing those patterns, it would be helpful if you knew enough about them to understand the naming conventions (ie - what a processor is, what a route is, etc).

### quickstart 
This will walk you through the process of building and deploying a 'hello-world' service. 

1. If you haven't already done so, clone this repository and check out the prerequisite section of this doc to ensure you've got all the things. 

2. At the command line, from the root project directory, build all the things with `mvn clean install`. 

3. Run the builder script to create a new element `./arch.py hello-world-bean`. You will be presented with a menu asking you what kind of thing you want to make. Select `0` for `bean`. It will then ask you to confirm a maven command that will be executed on your behalf. Press `[enter]` to accept. 
![bean creation screenshot](./kcar_readme_images/kcar_create_bean.png)
   
4. The runner script executed a maven command on your behalf to create an instance of the `bean archetype` named `hello-word-bean` a sub-project of the `elements` module. While not 100% necessary, best practice is to add the version of the newly created bean to the project parent pom file to ensure anyone other route that employs the newly created bean will also use the current 'release' version of it. *NOTE* This step will be automated in a future release. 
![add version to parent pom screenshot](./kcar_readme_images/add_hello_world_bean_version_to_parent_pom.png)
   
5. update the bean and bean test to respond with 'hello world!' instead of 'foo'

update the bean
![update bean to respond with hello world screenshot](./kcar_readme_images/hello_world_bean_update.png)
   
update the test
![update bean test to check for hello world response screenshot](./kcar_readme_images/hello_world_bean_update_test.png)


5. 




### core concepts

* element

* route

* chassis 

* service


### important conventions 

* project build files

* namespacing and properties


### how do I... ?

* make a new element?

* make a new route?

* make a new service?

* run a service locally 

* use a service's docker image locally

* deploy a service to heroku? 


### todo
in no particular order...
* add sample IT test to service chassis archetype that uses Docker 
* ensure logging configuration for all project components is sensible and consistent 
* investigate 'ivy-httpclient-shutdown-handler' noise that sometimes pops up during builds 
* add remaining archetypes to runner script
* update runner script to be a full CLI
* add hooks into pom files to ensure test reports are aggregated and readily available for jenkins (or whatever) to parse
* update runner script to add version information to global parent pom on module creation  
* update runner script to remove components from poms
* update runner script to manage project component versions 

### notes 

This project is the successor to an earlier project called [camel-harness](https://github.com/davidholiday/camel-harness). Many of the ideas developed in that project are here, only better. 


