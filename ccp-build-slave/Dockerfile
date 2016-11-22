FROM jenkinsci/ssh-slave
RUN apt-get update && DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends \
      python3-dev                                    \
      python-pip                                     \
      python-dev                                     \
      python-netaddr                                 \
      software-properties-common                     \
      python-setuptools                              \
      gcc                                            \
      git
WORKDIR /root
RUN git clone https://git.openstack.org/openstack/fuel-ccp
RUN pip install ./fuel-ccp/
