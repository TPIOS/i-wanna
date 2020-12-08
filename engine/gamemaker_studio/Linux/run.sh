#!/bin/bash
chmod +x runner
LD_LIBRARY_PATH=./lib:$LD_LIBRARY_PATH ./runner
