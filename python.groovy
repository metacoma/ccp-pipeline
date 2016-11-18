package com.mirantis.python

class VirtualEnv implements Serializable  {
  private String path;
  private ctx;

  @NonCPS
  def _init() {
    this.ctx.sh "test -d " + this.path + "|| virtualenv " + this.path
  }

  @NonCPS
  def run(cmd) {
    String venvCmd = ". " + this.path + "/bin/activate && " + cmd
    this.ctx.echo venvCmd
    this.ctx.sh venvCmd
  }

  def install(requirement) {
    this.run 'pip install ' + requirement
  }

  def VirtualEnv(ctx, path) {
    this.ctx = ctx
    this.path = path
  }

  def getPath() {
    return this.path
  }

}

