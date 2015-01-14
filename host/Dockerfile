FROM dockerfile/java:oracle-java8

RUN apt-add-repository -y ppa:jon-severinsson/ffmpeg && \
apt-get update && \
apt-get install -y git mercurial python-setuptools xvfb xfonts-base xfonts-75dpi xfonts-100dpi xfonts-cyrillic gource ffmpeg lsof apache2
RUN easy_install mercurial_keyring

RUN a2enmod proxy proxy_http headers

ADD ./apache-conf/virtualhost.conf /etc/apache2/sites-available/80-virtualhost.conf
ADD ./gource_generator.bash /gource_generator.bash

RUN a2ensite 80-virtualhost
RUN a2dissite 000-default.conf

RUN echo "[trusted]" > /etc/mercurial/hgrc.d/trust.rc
RUN echo "groups = staff" >> /etc/mercurial/hgrc.d/trust.rc

ENV JAR_NAME repovisualizer-0.0.1.jar
ENV EXTERNAL_PORT 8080
ENV EXTERNAL_ADDRESS localhost

VOLUME ["/ssh", "/artifacts, "/reposRoot", "/avatars", "/videoResults"]

EXPOSE 80

CMD rm -rf /root/.ssh && mkdir /root/.ssh && cp /ssh/known_hosts /root/.ssh/ && service apache2 start && java -jar /artifacts/${JAR_NAME} --port=8080 --external.server.address=${EXTERNAL_ADDRESS} --external.server.port=${EXTERNAL_PORT} --video.download.url.pattern=video/#REPO_NAME#.mp4 --repos.home=/reposRoot --video.results.dir=/videoResults --video.generator.script.path=/gource_generator.bash
