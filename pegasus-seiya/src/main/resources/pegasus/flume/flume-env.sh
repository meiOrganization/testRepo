# If this file is placed at FLUME_CONF_DIR/flume-env.sh, it will be sourced
# during Flume startup.

# Enviroment variables can be set here.

# export JAVA_HOME=/usr/lib/jvm/java-6-sun

# Give Flume more memory and pre-allocate, enable remote monitoring via JMX
#export JAVA_OPTS="-Xms25g -Xmx25g"
export JAVA_OPTS="-Xms3g -Xmx3g"

# Note that the Flume conf directory is always included in the classpath.
#FLUME_CLASSPATH=""