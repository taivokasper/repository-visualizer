FROM dockerfile/java:oracle-java8

RUN apt-get update
RUN apt-get install -y git maven nodejs-legacy nodejs npm ruby
RUN npm install -g grunt-cli bower
RUN gem install sass

VOLUME ["/artifact", "/source", "/root/.m2"]

CMD rm -rf repository-visualizer && \
cp -r /source repository-visualizer && \
cd repository-visualizer && \
npm install && \
grunt build && \
mvn clean package && \
cp target/repovisualizer-0.0.1.jar /artifact/
