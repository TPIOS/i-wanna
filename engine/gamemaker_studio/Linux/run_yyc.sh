#!/bin/bash
chmod +x ${YYPackageName}
LD_LIBRARY_PATH=./lib:$LD_LIBRARY_PATH ./${YYPackageName}
