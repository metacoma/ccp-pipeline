package com.mirantis.devops

import com.mirantis.python.VirtualEnv

class KargoEnv implements Serializable  {
  private String name;
  private ctx
  private venv
  private int slaveCount = 3
  private ubuntuIsoUrl = "http://172.16.180.122/images/ubuntu-1604-server-34.qcow2"

  @NonCPS
  def init() {
    this.ctx.sh "test -d " + this.path + "|| virtualenv " + this.path
  }

  @NonCPS
  def run(cmd) {
    String venvCmd = ". " + this.path + "/bin/activate && " + cmd
    this.ctx.echo venvCmd
    this.ctx.sh venvCmd
  }

  @NonCPS
  def deploy() {
    this.initVenv()
    this.ctx.sh 'git clone https://review.openstack.org/openstack/fuel-ccp-installer'
    // TODO refact
    deployCmd = 'export ENV_NAME=' + this.name + ';' +
      'export IMAGE_PATH=' + this.ubuntuIsoUrl + ';' +
      'export DONT_DESTROY_ON_SUCCESS=1;' +
      'export DEPLOY_METHOD="kargo"' +
      'export SLAVE_COUNT=' + this.slaveCount + ';'
      'export WORKSPACE=' + this.ctx.env.WORKSPACE + ';' +
      'cd fuel-ccp-installer && bash -x "./utils/jenkins/run_k8s_deploy_test.sh"

    this.ctx.sh deployCmd
  }

  // init venv for fuel-devops
  @NonCPS
  def initVenv() {
    venv.init()
    venv.install('psycopg2')
    venv.install('git+https://github.com/openstack/fuel-devops.git@3.0.1 --upgrade')
  }

  def KargoEnv(ctx, name) {
    this.ctx = ctx
    this.name = name
    this.venv = new com.mirantis.python.VirtualEnv(ctx, this.ctx.env.WORKSPACE + ".venv/")
  }

  // TODO: refact
  def getAdminNode() {
    cmd = 'ENV_NAME=' + this.name + ' python fuel-ccp-installer/utils/jenkins/env.py get_slaves_ip | cut -d, -f1 | sed "/[^0-9\.]//"'
    return this.ctx.sh script: cmd, returnStdout: true
  }

  // TODO: refact
  def run(cmd) {
    this.ctx.sh 'sshpass -pvagrant ssh vagrant@' + this.getAdminNode() + " " + cmd
  }

  // ubuntuIsoUrl setters && getter
  // TODO: pass and validate checksum
  def setUbuntuIsoUrl(url) {
    assert url
    this.ubuntuIsoUrl = url
  }

  def getUbuntuIsoUrl() {
    return this.ubuntuIsoUrl
  }


  // slaveCount setters && getter
  def setSlaveCount(slaveCount) {
    assert slaveCount >= 1
    this.slaveCount = slaveCount
  }

  def getSlaveCount() {
    return this.slaveCount

  // Name setters && getter
  def setName(name) {
    assert name
    this.name = name
  }

  def getName() {
    return this.name
  }

}

