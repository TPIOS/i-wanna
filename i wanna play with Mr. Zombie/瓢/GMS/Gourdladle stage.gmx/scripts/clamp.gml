///clamp(val,min,max);
///With this function you can maintain an input value between a specified range.
///argument0 - the value to clamp.
///argument1 - the minimum value to clamp between.
///argument2 - the maximum value to clamp between.

var clampVal,clampMin,clampMax;
clampVal = argument0;
clampMin = argument1;
clampMax = argument2;

if (clampVal <= clampMin) return clampMin;
else if (clampVal >= clampMax) return clampMax;
else return clampVal;
