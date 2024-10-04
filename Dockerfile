FROM opensearchstaging/opensearch:2.17.1

COPY ./build/distributions/opensearch-ubi-2.17.1.0.zip /tmp/

RUN /usr/share/opensearch/bin/opensearch-plugin install --batch telemetry-otel
RUN /usr/share/opensearch/bin/opensearch-plugin install --batch file:/tmp/opensearch-ubi-2.17.1.0.zip
