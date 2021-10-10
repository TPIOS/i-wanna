// set_default(value, default)

var value, _default;

value = argument0
_default = argument1

if (is_string(value)) {
    if (value != '') {
        return value
    } else {
        return _default
    }
}

if (is_real(value)) {
    if (value != 0) {
        return value
    } else {
        return _default
    }
}

