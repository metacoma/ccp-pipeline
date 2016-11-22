FROM jenkinsci/ssh-slave
RUN apt-get update && DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends \
      python3-dev                                    \
      python-pip                                     \
      python-dev                                     \
      python-netaddr                                 \
      software-properties-common                     \
      python-setuptools                              \
      gcc                                            \
      sudo                                           \
      git                                          &&\
      apt-get clean                                &&\
      rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*
RUN echo 'jenkins ALL=(ALL) NOPASSWD:ALL' >> /etc/sudoers
WORKDIR /root
RUN git clone https://git.openstack.org/openstack/fuel-ccp && pip install ./fuel-ccp/ && rm -rf fuel-ccp
