# RepoVisualizer
RepoVisualizer is a web application that in the background uses [Gource](https://code.google.com/p/gource) to generate visualisations of source code repositories.

## Configuration
Copy "fig.yml.sample" to "fig.yml" and edit it's contents to suit your needs.

## Running and building
Use the below commands to get up and running straight from the source code.
We use Docker container for building so your server will stay clean!
```sh
$ chmod +x update-and-start.sh
$ ./update-and-start.sh
```
## Requirements
* [Docker](https://www.docker.com)
* [Fig](http://www.fig.sh)
