apt-get -y install wireshark

echo -e "
set -o vi
export DISPLAY=host.docker.internal:0
export LIBGL_ALWAYS_INDIRECT=1
mkdir /tmp/foobar
export XDG_RUNTIME_DIR=/tmp/foobar
" >> /root/.bashrc
