FROM opensearchstaging/opensearch:2.15.0

COPY ./build/distributions/opensearch-ubi-2.15.0.0.zip /tmp/

RUN /usr/share/opensearch/bin/opensearch-plugin install --batch file:/tmp/opensearch-ubi-2.15.0.0.zip
