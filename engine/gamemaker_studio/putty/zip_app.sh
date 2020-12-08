#!/usr/bash
YYDir="${YYMacOutputDir}"
YYDir=${YYDir// /_spc_}
eval YYDir=${YYDir}
YYDir=${YYDir//_spc_/ }
pushd "$YYDir" >/dev/null
zip -r -q "${YYMacDisplayName}.app.zip" "${YYMacDisplayName}.app"
popd >/dev/null


