package com.mirantis.ccpEnv

class CcpEnv implements Serializable  {
  private ctx;
  private kargoEnv


  // TODO: implement refSpec usage
  def buildImage(image, refSpec) {
    this.kargoEnv.run('ccp build -c ' + image)

  }

  // FIXME: stub
  def imageIsAvailable(image) {
    return true
  }

  def deployImage(image) {
    if (this.imageIsAvailable(image)) {
      this.kargoEnv.run('ccp deploy -c ' + image)
    }
  }


  def registryExists() {
    this.kargoEnv.run('docker ps | grep registry')
  }

  def deployRegistry() {
    this.kargoEnv.run('fuel-ccp/tools/registry/deploy-registry.sh')
    this.kargoEnv.run('cat fuel-ccp/etc/topology-example.yaml >> ~/.ccp.yaml')
  }

  def buildImages() {
    this.kargoEnv.run('test -d fuel-ccp || (git clone fuel-ccp && sudo pip install fuel-ccp)')
    if !this.registryExists() {
      this.deployRegistry()
    }
    this.kargoEnv.run('ccp fetch')
    // TODO: check env.GERRIT_PROJECT for rebuid specific container
    memcached|rabbitmq|galera|stacklight|etcd
    ['memcahed', 'etcd', 'galera', 'rabbitmq'].each {
      // TODO pass refSpec instead null
      this.buildImage(${it}, null)
    }

    ['keystone', 'nova', 'glance', 'neutron', 'cinder', 'heat', 'horizon'].each {
      // TODO pass refSpec instead null
      this.buildImage(${it}, null)
    }

  }


  def deployImages() {
    ['memcahed', 'etcd', 'galera', 'rabbitmq'].each {
      // TODO pass refSpec instead null
      this.deployImage(${it}, null)
    }
    ['keystone', 'nova', 'glance', 'neutron', 'cinder', 'heat', 'horizon'].each {
      // TODO pass refSpec instead null
      this.deployImage(${it}, null)
    }

  }

  def CcpEnv(ctx, kargoEnv) {
    assert ctx && kargoEnv
    this.ctx = ctx
    this.kargoEnv kargoEnv
  }


}

