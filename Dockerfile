FROM openjdk:8

ENV STREAMER_HOME /usr/local/streamer

WORKDIR ${STREAMER_HOME}

COPY target/sse-1.0.jar ./see-1.0.jar

# ADD lib/postgres.jar $JETTY_HOME/lib/ext/postgres.jar
# ADD lib/mysql.jar $JETTY_HOME/lib/ext/mysql.jar

ADD entripoint.sh entripoint.sh
RUN chmod u+x entripoint.sh

EXPOSE 8080

ENTRYPOINT ["./entripoint.sh"]
CMD ["./default.properties"]