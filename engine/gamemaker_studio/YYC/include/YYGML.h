#ifndef __YYGML_H__
#define __YYGML_H__


#include "YYStd.h"
#include "Ref.h"
#include <string.h>
#include <stdlib.h>
#include <stddef.h>
#include <limits.h>
#include <float.h>
#include <math.h>

#if defined(YYLLVM)
# if defined(_MSC_VER) || (__cplusplus > 199711L)
#  if defined(_MSC_VER) && (_MSC_VER < 1700)
#  error Visual Studio 2012 should be used for compiling YYC 
#  endif
// Lambda style (for MSVC et al)
#define YYCOMPOUNDEXPR_BEGIN	[&](){
#define YYCOMPOUNDEXPR_RETURN	return
#define YYCOMPOUNDEXPR_END		;}()
# else
// GCC style
#define YYCOMPOUNDEXPR_BEGIN	({
#define YYCOMPOUNDEXPR_RETURN	
#define YYCOMPOUNDEXPR_END		;})
# endif
#endif

#define ARRAY_INDEX_NO_INDEX	INT_MIN

#define POINTER_INVALID			((void*)-1)
#define POINTER_NULL			(NULL)

#if defined(YYLLVM) || defined(__YYLLVM__)
struct SYYStackTrace
{
	SYYStackTrace* pNext;
	const char* pName;
	int line;
static SYYStackTrace*	s_pStart;
	SYYStackTrace( const char* _pName, int _line )
	{
		pName = _pName;
		line = _line;
		pNext = s_pStart;
		s_pStart = this;
	} // end constructor

	~SYYStackTrace() {
		s_pStart = pNext;
	} // end destructor
};
#define YY_STACKTRACE_FUNC_ENTRY( _name, _line )	SYYStackTrace __stackFunc( _name, _line )
#define YY_STACKTRACE_LINE( _line )					__stackFunc.line = _line
#else
#define YY_STACKTRACE_FUNC_ENTRY( _name, _line )
#define YY_STACKTRACE_LINE( _line )
#endif

//#if defined(YYLLVM)
// from math.h
YYCEXTERN double yyfabs( double _val);
YYCEXTERN double yyfdiv( int64 _lhs, int64 _rhs );
//#endif

class YYObjectBase;
class CInstance;
class IBuffer;
struct RValue;
struct YYRValue;

struct SWithIterator
{
	YYObjectBase* pOriginalSelf;
	YYObjectBase* pOriginalOther;

	YYObjectBase** ppBufferBase;
	YYObjectBase** ppCurrent;

	~SWithIterator()
	{
		if (ppBufferBase != NULL) {
			YYFree(ppBufferBase);
			ppBufferBase = NULL;
		} // end if
	}
};
typedef void (*GML_Call)(RValue& Result, CInstance* selfinst, CInstance* otherinst, int argc, RValue* arg);

extern bool Argument_Relative;
YYCEXTERN	int		YYCompareVal(const RValue& val1, const RValue& val2, double _prec);
YYCEXTERN	bool  Variable_GetValue_Direct(YYObjectBase* inst, int var_ind, int array_ind, RValue *val);
YYCEXTERN	bool  Variable_SetValue_Direct(YYObjectBase* inst, int var_ind, int array_ind, RValue *val);
YYCEXTERN	void	Variable_Global_SetVar(int var_ind, int arr_ind, RValue  * val);
YYCEXTERN	bool	Variable_Global_GetVar(int var_ind, int arr_ind,  RValue * val);
YYCEXTERN	bool  	YYGML_Variable_GetValue(int obj_ind, int var_ind,int array_ind, RValue *res);
YYCEXTERN	bool  	YYGML_Variable_SetValue(int obj_ind, int var_ind,int array_ind, RValue *res);
YYCEXTERN	bool  	Variable_GetValue(int obj_ind, int var_ind,int array_ind, RValue *res);
YYCEXTERN	bool  	Variable_SetValue(int obj_ind, int var_ind,int array_ind, RValue *res);
YYCEXTERN YYRValue* ARRAY_LVAL_RValue(YYRValue* pV, int _index );
//YYCEXTERN void FREE_RValue( RValue* p );
FORCEINLINE void FREE_RValue__Pre( RValue* p );
#define FREE_RValue(rvp)     do { RValue *__p = (rvp); if (((__p->kind-1)&(MASK_KIND_RVALUE & ~3))==0) { FREE_RValue__Pre(__p); } __p->flags = 0; __p->kind = VALUE_UNDEFINED; __p->ptr = NULL; } while (0);

//YYCEXTERN void COPY_RValue( RValue* pD, const RValue* pS);
YYCEXTERN void YYError( const char* _error, ... );
YYCEXTERN void YYprintf( const char* _error, ... );
YYCEXTERN char* yyitoa( int _n, char* _pDest, int _radix );
YYCEXTERN void STRING_RValue( char** _ppCurrent, char** _pBase, int* _pMaxLen, const RValue* _pV );
YYCEXTERN int64 INT64_RValue( const RValue* _pV );
YYCEXTERN int32 INT32_RValue( const RValue* _pV );
YYCEXTERN bool BOOL_RValue( const RValue* _pV );
YYCEXTERN double REAL_RValue_Ex(const RValue* _pV );

FORCEINLINE double REAL_RValue(const RValue *_pV);

YYCEXTERN void* PTR_RValue(const RValue* _pV );

// this will need to be a get / set
extern double g_GMLMathEpsilon;





YYCEXTERN int YYGML_NewWithIterator( SWithIterator* pIterator, YYObjectBase** ppSelf, YYObjectBase** ppOther, int _objind );
YYCEXTERN bool YYGML_WithIteratorNext( SWithIterator* pIterator, YYObjectBase** ppSelf, YYObjectBase** ppOther );
YYCEXTERN void YYGML_DeleteWithIterator( SWithIterator* pIterator, YYObjectBase** ppSelf, YYObjectBase** ppOther );

YYCEXTERN double YYGML_random( double _v );
YYCEXTERN int YYGML_irandom( int _range );
YYCEXTERN double YYGML_random_range( double _base, double _end );
YYCEXTERN int YYGML_irandom_range( int _base, int _end );

YYCEXTERN double YYGML_random_old( double _v );
YYCEXTERN int YYGML_irandom_old( int _range );
YYCEXTERN double YYGML_random_range_old( double _base, double _end );
YYCEXTERN int YYGML_irandom_range_old( int _base, int _end );

YYCEXTERN void YYGML_sprite_set_cache_size( int _sprite_index, int _cache_size );
YYCEXTERN bool YYGML_keyboard_check_direct( int _key );
YYCEXTERN char* YYGML_string( const RValue& _val );
YYCEXTERN char* YYGML_AddString( const char* _first, const char* _second );
YYCEXTERN YYRValue& YYGML_AddVar( const YYRValue& _first, const YYRValue& _second );

YYCEXTERN int YYGML_BUFFER_Write(int buffer_index, int value_type, const YYRValue &val);

YYCEXTERN double YYGML_StringByteAt(const char *string, int _index);

YYCEXTERN void YYGML_action_set_relative( bool _enabled );
YYCEXTERN void YYGML_action_move( CInstance* _pSelf, const char* _dir, float _speed );
YYCEXTERN void YYGML_action_set_gravity( CInstance* _pSelf, float _dir, float _gravity );
YYCEXTERN void YYGML_action_move_point( CInstance* _pSelf, float _x, float _y, float _speed );
YYCEXTERN void YYGML_action_reverse_xdir( CInstance* _pSelf );
YYCEXTERN void YYGML_action_reverse_ydir( CInstance* _pSelf );
YYCEXTERN void YYGML_action_path( CInstance* _self, int _path, float _speed, int _endact, bool _relative );
YYCEXTERN void YYGML_action_linear_step( CInstance* _self, float _x, float _y, float _speed, bool _checkall);
YYCEXTERN void YYGML_action_kill_object(CInstance* _self);
YYCEXTERN void YYGML_action_create_object(CInstance* _pSelf, int _ind, float _x, float _y);
YYCEXTERN void YYGML_action_create_object_motion(CInstance* _pSelf, int _ind, float _x, float _y, float _speed, float _dir);
YYCEXTERN void YYGML_action_change_object(CInstance* _self, int _objind, bool _performEvents);
YYCEXTERN void YYGML_action_sound(int _soundid, bool _loop);
YYCEXTERN void YYGML_action_end_sound(int _soundid);
YYCEXTERN void YYGML_action_next_room( void );
YYCEXTERN void YYGML_action_set_alarm(CInstance* _pSelf, int _alarm, int _val);
YYCEXTERN void YYGML_action_end_game( void );
YYCEXTERN void YYGML_action_restart_game( void );
YYCEXTERN bool YYGML_action_if_number( int _objectID, double _val, int _typeIf);
YYCEXTERN bool YYGML_action_if_variable( const RValue& _val1, const RValue& _val2, int _typeIf);
YYCEXTERN void YYGML_action_fullscreen( int _type );
YYCEXTERN void YYGML_ini_open( const char* _pFilename );
YYCEXTERN char* YYGML_ini_close( void );
YYCEXTERN double YYGML_ini_read_real( const char* _pSection, const char* _pKey, double _default);
YYCEXTERN void YYGML_ini_write_real(const char* _pSection, const char* _pKey, double _value);
YYCEXTERN bool YYGML_place_free(CInstance* _self, float _x, float _y);
YYCEXTERN void YYGML_move_snap(CInstance* _self, float _x, float _y);
YYCEXTERN void YYGML_motion_set(CInstance* pSelf, float _direction, float _speed);
YYCEXTERN bool YYGML_instance_exists( CInstance* _self, CInstance* _other, int _objind );
YYCEXTERN bool YYGML_position_meeting(CInstance* _self, CInstance* _other, float _x, float _y, int ind);
YYCEXTERN int YYGML_instance_create(float _x, float _y, int _objind);
YYCEXTERN void YYGML_instance_change(CInstance* _self, int _objind, bool _performEvents);
YYCEXTERN void YYGML_instance_destroy(CInstance* _self, CInstance* _other, int _count, YYRValue** _args);
YYCEXTERN int YYGML_instance_number(CInstance* pSelf, CInstance* pOther, int _objectID);
YYCEXTERN void YYGML_event_inherited(CInstance* pSelf, CInstance* pOther);
YYCEXTERN void YYGML_event_perform(CInstance* pSelf, CInstance* pOther, int etype, int enumb);
YYCEXTERN void YYGML_event_object(CInstance* pSelf, CInstance* pOther, int objId, int etype, int enumb);
YYCEXTERN void YYGML_event_user(CInstance* pSelf, CInstance* pOther, int enumb);
YYCEXTERN void YYGML_room_goto_next( void );
YYCEXTERN void YYGML_game_restart( void );
YYCEXTERN void YYGML_room_restart( void );
YYCEXTERN void YYGML_game_end( void );
YYCEXTERN void YYGML_window_set_caption( const char* _pStr );
YYCEXTERN int YYGML_make_color_rgb(int _red, int _green, int _blue );
YYCEXTERN int YYGML_color_get_red(int _color);
YYCEXTERN int YYGML_color_get_green(int _color);
YYCEXTERN int YYGML_color_get_blue(int _color);
YYCEXTERN void YYGML_draw_set_blend_mode( int _blendmode );
YYCEXTERN void YYGML_draw_set_halign(int _type);
YYCEXTERN void YYGML_draw_set_valign(int _type );
YYCEXTERN void YYGML_draw_self( CInstance* _pSelf );
YYCEXTERN void YYGML_draw_text_transformed(float _x, float _y, const char* _pStr, float _xscale, float _yscale, float _angle);
YYCEXTERN void YYGML_draw_text_color(float _x, float _y, const char* _pStr, int _colTL, int _colTR, int _colBR, int _colBL, float _alpha );
YYCEXTERN void YYGML_draw_text_transformed_color(float _x, float _y, const char* _pStr, float _xscale, float _yscale, float _angle, int _colTL, int _colTR, int _colBR, int _colBL, float _alpha);
YYCEXTERN void YYGML_draw_text_ext_color(float _x, float _y, const char* _pStr, int _linesep, int _linewidth, int _colTL, int _colTR, int _colBR, int _colBL, float _alpha );
YYCEXTERN void YYGML_draw_text_ext_transformed_color(float _x, float _y, const char* _pStr, int _linesep, int _linewidth, float _xscale, float _yscale, float _angle, int _colTL, int _colTR, int _colBR, int _colBL, float _alpha);
YYCEXTERN void YYGML_draw_point(float _x1, float _y1);
YYCEXTERN void YYGML_draw_point_ext(float _x1, float _y1, unsigned int _c1);
YYCEXTERN void YYGML_draw_rectangle(float _x1, float _y1, float _x2, float _y2, bool _outline);
YYCEXTERN void YYGML_draw_sprite( CInstance* _pSelf, int _sprite_index, int _image_index, float _x, float _y );
YYCEXTERN void YYGML_draw_sprite_part( CInstance* _pSelf, int _sprite_index, int _image_index, float _left, float _top, float _width, float _height, float _x, float _y );
YYCEXTERN void YYGML_draw_sprite_ext( CInstance* _pSelf, int _sprite_index, int _image_index, float _x, float _y, float _xscale, float _yscale, float _angle, int _colour, float _alpha );
YYCEXTERN void YYGML_draw_set_font( int _font_index );
YYCEXTERN void YYGML_draw_text( float _x, float _y, const char* _string );
YYCEXTERN void YYGML_draw_set_colour( unsigned int _color );
YYCEXTERN void YYGML_draw_set_alpha( float _alpha );
YYCEXTERN bool YYGML_draw_surface_part_ext(int _id, float _xo, float _yo, float _w, float _h, float _x, float _y, float _xscale, float _yscale, int _color, float _alpha);
YYCEXTERN bool YYGML_surface_set_target(int _surfaceid);
YYCEXTERN bool YYGML_surface_set_target_ext(int _stage, int _id);
YYCEXTERN bool YYGML_surface_reset_target(void);

YYCEXTERN void YYGML_shader_set(int _shader);
YYCEXTERN void YYGML_shader_reset();
YYCEXTERN void YYGML_shader_set_uniform_i(int _count, YYRValue** _args);
YYCEXTERN void YYGML_shader_set_uniform_f(int _count, YYRValue** _args);

//void YYGML_draw_sprite_ext();
YYCEXTERN bool YYGML_keyboard_check(int _key);
//void YYGML_keyboard_check_direct();
YYCEXTERN int YYGML_joystick_direction( int _joystick );
YYCEXTERN bool YYGML_joystick_check_button(int _joystick, int _button );
//void YYGML_random();
YYCEXTERN double YYGML_abs( double _val );
YYCEXTERN double YYGML_sign( double _val );
YYCEXTERN double YYGML_cos(double _val );
YYCEXTERN double YYGML_degtorad( double _val );
YYCEXTERN YYRValue& YYGML_choose(YYRValue& _result, int _count, YYRValue** _args);
YYCEXTERN YYRValue& YYGML_max(YYRValue& _result, int _count, YYRValue** _args);
YYCEXTERN YYRValue& YYGML_min(YYRValue& _result, int _count, YYRValue** _args);
YYCEXTERN int YYGML_ord( const char* _pStr );
YYCEXTERN float YYGML_point_direction( float _x1, float _y1, float _x2, float _y2);
YYCEXTERN float YYGML_lengthdir_x(float _deltaX, float _deltaY);
YYCEXTERN float YYGML_lengthdir_y(float _deltaX, float _deltaY);
YYCEXTERN void YYGML_sound_play(int _soundid);
YYCEXTERN void YYGML_sound_stop(int _soundid);
YYCEXTERN void YYGML_show_debug_message( const YYRValue& _val );

// common functions used for models in st00pid scripts
YYCEXTERN void YYGML_vertex_normal(int _buffer, float _x, float _y, float _z);
YYCEXTERN void YYGML_vertex_position(int _buffer, float _x, float _y);
YYCEXTERN void YYGML_vertex_position_3d(int _buffer, float _x, float _y, float _z);
YYCEXTERN void YYGML_vertex_colour(int _buffer, int _col, float _alpha);
YYCEXTERN void YYGML_vertex_texcoord(int _buffer, float _u, float _v);
YYCEXTERN void YYGML_vertex_argb(int _buffer, unsigned int _col);
YYCEXTERN void YYGML_vertex_end(int _buffer);
YYCEXTERN void YYGML_vertex_begin(int _buffer, int _format);
YYCEXTERN void YYGML_vertex_float1(int _buffer, float _x);
YYCEXTERN void YYGML_vertex_float2(int _buffer, float _x, float _y);
YYCEXTERN void YYGML_vertex_float3(int _buffer, float _x, float _y, float _z);
YYCEXTERN void YYGML_vertex_float4(int _buffer, float _x, float _y, float _z, float _w);
YYCEXTERN void YYGML_vertex_ubyte4(int _buffer, int _x, int _y, int _z, int _w);

YYCEXTERN int YYGML_ds_grid_create( int _width, int _height );
YYCEXTERN void YYGML_ds_grid_set( int _index, int _x, int _y, const YYRValue& _val );
YYCEXTERN YYRValue& YYGML_ds_grid_get( int _index, int _x, int _y);
YYCEXTERN int YYGML_ds_stack_create( void );
YYCEXTERN void YYGML_ds_stack_push( int _count, YYRValue** _args);
YYCEXTERN YYRValue& YYGML_ds_stack_pop( int _index );
YYCEXTERN int YYGML_ds_map_add(int _index, const YYRValue& _key, const YYRValue& _val);

// This function routes any unknown functions to the correct destination
YYCEXTERN YYRValue& YYGML_CallLegacyFunction( CInstance* _pSelf, CInstance* _pOther, YYRValue& _result, int _argc, int _id, YYRValue** _args );
YYCEXTERN YYRValue& YYGML_CallScriptFunction( CInstance* _pSelf, CInstance* _pOther, YYRValue& _result, int _argc, int _id, YYRValue** _args );
YYCEXTERN YYRValue& YYGML_CallExtensionFunction( CInstance* _pSelf, CInstance* _pOther, YYRValue& _result, int _argc, int _id, YYRValue** _args );
inline void YYOpError( const char* pOp, const YYRValue* _lhs, const YYRValue* _rhs );


struct YYVAR
{
	const char* pName;
	int val;
};

#ifndef NULL
#define NULL	0
#endif

#if defined(YYLLVM) && !defined(__defined_YYString__)
#define __defined_YYString__
struct YYString
{
	const char* pStr;

	// default constructor
	//YYString() {
	//	pStr = NULL;
	//} // end YYString

	YYString( const char* _pMessage ) {
		pStr = YYStrDup( _pMessage );
	} // end _pMessage

	// copy constructor
	//YYString( const YYString& _s ) {
	//	pStr = YYStrDup( _s.pStr );
	//} // end YYString

	// destructor
	~YYString() {
		if (pStr != NULL) {
			YYStrFree( pStr );
			pStr = NULL;
		} // end if
	} // end destructor

	// cast operator to char*
	operator char* () {
		return (char*)pStr;
	} // end char*

	YYString& operator=( const YYString& _s ) {
		pStr = YYStrDup( _s.pStr );
		return *this;
	} // end operator=

	YYString& operator=( const char* _pMessage ) {
		pStr = YYStrDup( _pMessage );
		return *this;
	} // end operator=

	// handle +=
	YYString& operator+=( const YYString& rhs ) {
		char* pNew = (char*)YYGML_AddString( pStr, rhs.pStr );
		YYFree( (void*)pNew );
		pStr = pNew;
		return *this;
	} // end operator+=
};
#else
typedef char* YYString;
#endif

#if !defined(__defined_RValue_consts__)
#define __defined_RValue_consts__
const int VALUE_REAL= 0;		// Real value
const int VALUE_STRING= 1;		// String value
const int VALUE_ARRAY= 2;		// Array value
const int VALUE_OBJECT = 6;	// YYObjectBase* value 
const int VALUE_INT32= 7;		// Int32 value
const int VALUE_UNDEFINED= 5;	// Undefined value
const int VALUE_PTR= 3;			// Ptr value
const int VALUE_VEC3= 4;		// Vec3 (x,y,z) value (within the RValue)
const int VALUE_VEC4= 8;		// Vec4 (x,y,z,w) value (allocated from pool)
const int VALUE_VEC44= 9;		// Vec44 (matrix) value (allocated from pool)
const int VALUE_INT64= 10;		// Int64 value
const int VALUE_ACCESSOR = 11;	// Actually an accessor
const int VALUE_NULL = 12;	// JS Null
const int VALUE_BOOL = 13;	// Bool value
const int VALUE_ITERATOR = 14;	// JS For-in Iterator
const int VALUE_REF = 15;		// Reference value (uses the ptr to point at a RefBase structure)
#define MASK_KIND_RVALUE	0x0ffffff
const int VALUE_UNSET = MASK_KIND_RVALUE;

const int OBJECT_KIND_YYOBJECTBASE = 0;
const int OBJECT_KIND_CINSTANCE = 1;
const int OBJECT_KIND_ACCESSOR = 2;
const int OBJECT_KIND_SCRIPTREF = 3;

const int YYVAR_ACCESSOR_GET = (0);
const int YYVAR_ACCESSOR_SET = (1);

struct RValue;
struct DynamicArrayOfRValue
{
	int length;
	RValue* arr;
};

struct RefDynamicArrayOfRValue
{
	int	refcount;
	DynamicArrayOfRValue* pArray;
	void* pOwner;
	int visited;
	int length;
};

struct vec3
{
	float	x,y,z;
};

struct vec4
{
	float	x,y,z,w;
};

struct matrix44
{
	vec4	m[4];
};

const int ERV_None = 0;
const int ERV_Enumerable = (1<<0);
const int ERV_Configurable = (1<<1);
const int ERV_Writable = (1<<2);
const int ERV_Owned = (1<<3);

#define JS_BUILTIN_PROPERTY_DEFAULT				(ERV_Writable | ERV_Configurable )
#define JS_BUILTIN_LENGTH_PROPERTY_DEFAULT		(ERV_None)


#pragma pack( push, 4)
struct RValue
{
	union {
		int32 v32;
		int64 v64;
		double	val;						// value when real
		union {
			union {
				RefString* pRefString;
				//char*	str;						// value when string
				RefDynamicArrayOfRValue* pRefArray;	// pointer to the array
				vec4* pVec4;
				matrix44* pMatrix44;
				void* ptr;
				YYObjectBase* pObj;
				//vec3 v3;
			};
			/*struct {
				float x, y, z;
			} v3;*/
            //float v3[3];
		};
	};
	int		flags;							// use for flags (Hijack for Enumerable and Configurable bits in JavaScript)  (Note: probably will need a visibility as well, to support private variables that are promoted to object scope, but should not be seen (is that just not enumerated????) )
	int		kind;							// kind of value

	void Serialise(IBuffer* _buffer);
	void DeSerialise(IBuffer* _buffer);

	const char* GetString() const { return (((kind & MASK_KIND_RVALUE) == VALUE_STRING) && (pRefString != NULL)) ? pRefString->get() : ""; }
	long long asInt64() const { return INT64_RValue(this); }
	double asReal() const { return REAL_RValue(this); }
};


// new structure used to initialise constant numbers at global scope (to eliminate construction overhead).
struct DValue
{
	double	val;
	int		dummy;
	int		kind;
};

struct DLValue
{
	int64	val;
	int		dummy;
	int		kind;
};

#pragma pack(pop)
#endif

#if defined(YYLLVM) && !defined(__defined_CInstanceBase__)
#define __defined_CInstanceBase__
class CInstanceBase
{
public:
	YYRValue*		yyvars;
	virtual ~CInstanceBase() {};
#if _MSC_VER != 1500
	YYRValue& GetYYVarRef(int index) { 	
		if (yyvars)	{ 
			return (YYRValue&)((RValue*)yyvars)[index];
		}
		return InternalGetYYVarRef(index);
	} // end GetYYVarRef
	virtual  YYRValue& InternalGetYYVarRef(int index)=0;
#else
	virtual  YYRValue& GetYYVarRef(int index)=0;
#endif
};
#endif

YYCEXTERN void DeterminePotentialRoot(YYObjectBase* _pContainer, YYObjectBase* _pObj);
YYCEXTERN YYObjectBase* GetContextStackTop();

// #############################################################################################
/// Function:<summary>
///          	Copy an RValue, taking a reference to the array when required...
///          </summary>
///
/// In:		<param name="_pDest">Copying TO</param>
///			<param name="_pSource">Copying FROM</param>
///				
// #############################################################################################
FORCEINLINE void COPY_RValue__Post( RValue* _pDest, const RValue* _pSource ) FORCEINLINE_ATTR;
FORCEINLINE void COPY_RValue__Post( RValue* _pDest, const RValue* _pSource )
{    
	_pDest->kind = _pSource->kind;
	_pDest->flags = _pSource->flags;
	switch( _pSource->kind&MASK_KIND_RVALUE ) {
	case VALUE_ARRAY:
		_pDest->pRefArray = _pSource->pRefArray;
		if (_pDest->pRefArray!=NULL) {
			++_pDest->pRefArray->refcount;
			if (_pDest->pRefArray->pOwner == NULL) _pDest->pRefArray->pOwner=_pDest;
		//	YYprintf(  "COPY - Refcount array at - %p(%d)\n", _pDest->pRefArray, _pDest->pRefArray->refcount );
		} 
		break;
	case VALUE_BOOL:
	case VALUE_REAL:
		_pDest->val = _pSource->val;
		break;
	case VALUE_INT32:
		_pDest->v32 = _pSource->v32;
		break;
	case VALUE_INT64:
		_pDest->v64 = _pSource->v64;
		break;
	case VALUE_PTR:
		_pDest->ptr = _pSource->ptr;
		break;
	case VALUE_STRING:
		_pDest->pRefString = RefString::assign(_pSource->pRefString);
		break;
	case VALUE_OBJECT:
	{
		_pDest->pObj = _pSource->pObj;

		if (_pSource->pObj != NULL)
		{
			//extern void MarkObjectAsCurrent(void* _pObj);
			//MarkObjectAsCurrent(_pDest->pObj);			

			YYObjectBase* pContainer = GetContextStackTop();
			DeterminePotentialRoot(pContainer, _pSource->pObj);
		}
	}
		break;
	case VALUE_ITERATOR:
		_pDest->ptr = _pSource->ptr;
		break;
	} 
} 

FORCEINLINE void COPY_RValue( RValue* _pDest, const RValue* _pSource )FORCEINLINE_ATTR;
FORCEINLINE void COPY_RValue( RValue* _pDest, const RValue* _pSource )
{    
	switch( (_pDest->kind&MASK_KIND_RVALUE) ) {
	case VALUE_STRING:
		_pDest->pRefString = RefString::remove(_pDest->pRefString);
		break;
	case VALUE_ARRAY:
		FREE_RValue( _pDest );
		break;
	} // end switch
    _pDest->ptr =NULL;    

	COPY_RValue__Post(_pDest, _pSource );
} 

class COwnedObject
{
public:
	virtual ~COwnedObject() {};
};

FORCEINLINE double REAL_RValue( const RValue* p )
{
	return ( (p->kind&MASK_KIND_RVALUE) == VALUE_REAL ? p->val : REAL_RValue_Ex(p));
}

FORCEINLINE void FREE_RValue__Pre( RValue* p )
{
	switch( p->kind&MASK_KIND_RVALUE ) {
	case VALUE_PTR:
		if (p->flags & ERV_Owned)
		{
			delete (COwnedObject*) p->ptr;
		}
		break;
	case VALUE_STRING:
		p->pRefString = RefString::remove(p->pRefString);
		break;
	case VALUE_ARRAY:
		RefDynamicArrayOfRValue* pArray = p->pRefArray;
		if (pArray != NULL) {

			YYCEXTERN void LOCK_RVALUE_MUTEX();
			YYCEXTERN void UNLOCK_RVALUE_MUTEX();
			LOCK_RVALUE_MUTEX();
			--pArray->refcount;
			if (pArray->pOwner == p) pArray->pOwner = NULL; 
			if (pArray->refcount <= 0) {
		//		YYprintf( "Freeing array at - %p\n", pArray );
				for( int n=0; n<pArray->length; ++n) {
					DynamicArrayOfRValue* pA = &pArray->pArray[n];
					RValue* pV = &pA->arr[0];
					RValue* pVE = &pA->arr[ pA->length ];
					for ( ; pV < pVE ; ++pV ) {
						FREE_RValue( pV );
					} // end for
					YYStrFree( (const char*)pA->arr );
					pA->arr = NULL;
				} // end for

				YYStrFree( (const char*)pArray->pArray );
				pArray->pArray = NULL;
				YYStrFree( (const char*)pArray );
				p->pRefArray = NULL;
			} // end if
		//	else {
		//		YYprintf( "FREE - Refcount array at - %p(%d)\n", pArray, pArray->refcount );
		//	} // end else
			UNLOCK_RVALUE_MUTEX();
		} // end if
		break;
	} // end switch
}

FORCEINLINE double yyfmod( double _lhs, double _rhs ) FORCEINLINE_ATTR;
FORCEINLINE double yyfmod( double _lhs, double _rhs ) 
{
	return fmod( _lhs, _rhs );
} // end yyfmod

FORCEINLINE double yyfmod( const RValue& _lhs, const RValue& _rhs ) FORCEINLINE_ATTR;
FORCEINLINE double yyfmod( const RValue& _lhs, const RValue& _rhs ) 
{
	return fmod( _lhs.asReal(), _rhs.asReal() );
} // end yyfmod
FORCEINLINE double yyfmod( const RValue& _lhs, double _rhs ) FORCEINLINE_ATTR;
FORCEINLINE double yyfmod( const RValue& _lhs, double _rhs ) 
{
	return fmod( _lhs.asReal(), _rhs );
} // end yyfmod
FORCEINLINE double yyfmod( double _lhs, const RValue& _rhs ) FORCEINLINE_ATTR;
FORCEINLINE double yyfmod( double _lhs, const RValue& _rhs ) 
{
	return fmod( _lhs, _rhs.asReal() );
} // end yyfmod
//FORCEINLINE double yyfmod( int64 _lhs, int64 _rhs ) FORCEINLINE_ATTR;
//FORCEINLINE double yyfmod( int64 _lhs, int64 _rhs ) 
//{
//	return (double)(_lhs % _rhs);
//} // end yyfmod

FORCEINLINE double yyfdiv( int64 _lhs, int64 _rhs ) FORCEINLINE_ATTR;
FORCEINLINE double yyfdiv( int64 _lhs, int64 _rhs )
{
	return (double) ((int64)_lhs / (int64)_rhs);
} // end yyfmod

FORCEINLINE double yyfabs( double _val ) FORCEINLINE_ATTR;
FORCEINLINE double yyfabs( double _val )
{
	return (_val < 0) ? -_val : _val;
} 

#if defined(YYLLVM) && !defined(__defined_YYRValue__)
#define __defined_YYRValue__
#define VARIABLE_ARRAY_MAX_DIMENSION 32000
struct YYRValue : RValue
{
	// --------------------------------------------------------------------------------------------------
	// destructors
	// --------------------------------------------------------------------------------------------------
	~YYRValue() {
		__localFree();
		//FREE_RValue__Pre( this );
	} // end YYRValue

	void __localFree( void )
	{
		if (((kind-1) & (MASK_KIND_RVALUE&~3)) == 0) {
			FREE_RValue__Pre( this );
		} // end if
	} // end __localFree

	void __localCopy( const YYRValue& _v ) {
		COPY_RValue__Post( this, &_v );
	} // end __localCopy


	// --------------------------------------------------------------------------------------------------
	// constructors
	// --------------------------------------------------------------------------------------------------
	// default constructor
	YYRValue() {
		kind = VALUE_UNDEFINED;
		ptr = NULL;
	} // end YYRValue

	YYRValue( const YYRValue& _v ) {
		__localCopy( _v );
	} // end YYRValue

	YYRValue( double _val ) {
		kind = VALUE_REAL;
		val = _val;
	}

	YYRValue( float _val ) {
		kind = VALUE_REAL;
		val = _val;
	}

	YYRValue( int _val ) {
		kind = VALUE_REAL;
		val = _val;
	}

	YYRValue( long long _val ) {
		kind = VALUE_REAL;
		val = (double)_val;
	}

	YYRValue( const char* _pStr ) {
		YYSetString( this, _pStr );
	}

	// --------------------------------------------------------------------------------------------------
	// unary negate
	// --------------------------------------------------------------------------------------------------
	YYRValue operator-() {
		YYRValue ret;
		ret.kind = kind;
		switch( (kind & MASK_KIND_RVALUE) ) {
		case VALUE_BOOL:
		case VALUE_REAL: ret.val = -val; break;
		case VALUE_INT32: ret.v32 = -v32; break;
		case VALUE_INT64: ret.v64 = -v64; break;
		default: YYError( "Invalid type for negate operation\n" ); break;
		} // end switch
		return ret;
	} // end operator-

	// --------------------------------------------------------------------------------------------------
	// assignment operators
	// --------------------------------------------------------------------------------------------------
	YYRValue& operator=( const YYRValue& _v ) {
		if (&_v != this) {
			DValue temp;
			memcpy(&temp, &_v, sizeof(YYRValue));
			bool fIsArray = ((temp.kind & MASK_KIND_RVALUE) == VALUE_ARRAY);
			if (fIsArray) ++((RValue*)&temp)->pRefArray->refcount;
			__localFree();
			if (fIsArray) --((RValue*)&temp)->pRefArray->refcount;
			__localCopy( *(YYRValue*)&temp );
		} // end if
		return *this;
	} // end operator=

	YYRValue& operator=( double _v ) {
		__localFree();
		kind = VALUE_REAL;
		val = _v;
		return *this;
	} // end operator=

	YYRValue& operator=( float _v ) {
		__localFree();
		kind = VALUE_REAL;
		val = _v;
		return *this;
	} // end operator=

	YYRValue& operator=( int _v ) {
		__localFree();
		kind = VALUE_REAL;
		val = _v;
		return *this;
	} // end operator=

	YYRValue& operator=( long long _v ) {
		__localFree();
		kind = VALUE_INT64;
		v64 = _v;
		return *this;
	} // end operator=

	YYRValue& operator=( const char* _pStr ) {
		__localFree();
		YYCreateString( this, _pStr );
		return *this;
	} // end operator=

	// --------------------------------------------------------------------------------------------------
	// cast operators
	// --------------------------------------------------------------------------------------------------
	operator bool() const {
		return REAL_RValue(this) > 0.5;
	} // end cast operator

	operator char*() const {
		return ((kind & MASK_KIND_RVALUE)== VALUE_STRING) ? ((pRefString!=NULL) ? (char*)pRefString->get() : NULL) : NULL;
	} // end cast operator

	operator const char*() const {
		return ((kind & MASK_KIND_RVALUE)== VALUE_STRING) ? ((pRefString!=NULL) ? pRefString->get() : NULL) : NULL;
	} // end cast operator

	operator double() const {
		return REAL_RValue(this);
	} // end cast operator

	operator float() const {
		return (float)REAL_RValue(this);
	} // end cast operator

	operator int() const {
		return INT32_RValue(this);
	} // end cast operator

	operator unsigned int() const {
		return (unsigned int)INT32_RValue(this);
	} // end cast operator

	operator long() const {
		return INT32_RValue(this);
	} // end cast operator

	operator unsigned long() const {
		return (unsigned long)INT32_RValue(this);
	} // end cast operator

	operator long long () const {
		return INT64_RValue(this);
	} // end cast operator	
	
	operator unsigned long long () const {
		return (unsigned long long)INT64_RValue(this);
	} // end cast operator	
	
	// --------------------------------------------------------------------------------------------------
	// operator  post ++ and pre ++
	// --------------------------------------------------------------------------------------------------
	YYRValue& operator++() {
		switch( (kind & MASK_KIND_RVALUE) ) {
		case VALUE_BOOL:
		case VALUE_REAL:
			++val;
			break;
		case VALUE_INT32:
			++v32;
			break;
		case VALUE_INT64:
			++v64;
			break;
		default:
			YYOpError( "++", this, this );
			break;
		} // end switch
		return *this;
	} // end operator++
	YYRValue operator++(int) {
		YYRValue tmp(*this);
		operator++();
		return tmp;
	} // end operator++

	// --------------------------------------------------------------------------------------------------
	// operator  post -- and pre --
	// --------------------------------------------------------------------------------------------------
	YYRValue& operator--() {
		switch( (kind & MASK_KIND_RVALUE) ) {
		case VALUE_BOOL:
		case VALUE_REAL:
			--val;
			break;
		case VALUE_INT32:
			--v32;
			break;
		case VALUE_INT64:
			--v64;
			break;
		default:
			YYOpError( "--", this, this);
			break;
		} // end switch
		return *this;
	} // end operator--
	YYRValue operator--(int) {
		YYRValue tmp(*this);
		operator--();
		return tmp;
	} // end operator--

	// --------------------------------------------------------------------------------------------------
	// operator + and +=
	// --------------------------------------------------------------------------------------------------
	YYRValue& operator+=( const YYRValue& rhs ) {
		switch( (kind & MASK_KIND_RVALUE) ) {
		case VALUE_BOOL:
		case VALUE_REAL:
			val += REAL_RValue(&rhs);
			break;
		case VALUE_INT32:
			v32 += INT32_RValue(&rhs);
			break;
		case VALUE_INT64:
			v64 += INT64_RValue(&rhs);
			break;
		case VALUE_STRING:
			{
				// if string then concatenate, if not then convert to string
				const char* pFirst = (pRefString != NULL) ? pRefString->get() : NULL;
				const char* pSecond = (rhs.pRefString != NULL) ? rhs.pRefString->get() : NULL;
				char* pNew = (char*)YYGML_AddString( pFirst, pSecond );
				YYCreateString( this, pNew );
				YYFree(pNew);
				break;
			} // end block
		default:
			YYOpError( "+=", this, &rhs );
			break;
		} // end switch
		return *this;
	} // end operator+=
	YYRValue& operator+=( const double rhs ) {
		switch( kind ) {
		case VALUE_BOOL:
		case VALUE_REAL:
			val += rhs;
			break;
		case VALUE_INT32:
			v32 += (int32) rhs;
			break;
		case VALUE_INT64:
			v64 += (int64) rhs;
			break;
		case VALUE_STRING:
			// if string then concatenate, if not then convert to string
			YYError( "unable to add a number to string" );
			break;
		} // end switch
		return *this;
	} // end operator+=
	YYRValue& operator+=( const int rhs ) {
		switch( kind ) {
		case VALUE_BOOL:
		case VALUE_REAL:
			//if (rhs.kind != VALUE_REAL){ error message }
			val += rhs;
			break;
		case VALUE_INT32:
			v32 += rhs;
			break;
		case VALUE_INT64:
			v64 += rhs;
			break;
		case VALUE_STRING:
			// if string then concatenate, if not then convert to string
			YYError( "unable to add a number to string" );
			break;
		} // end switch
		return *this;
	} // end operator+=
	YYRValue& operator+=( const long long rhs ) {
		switch( kind ) {
		case VALUE_BOOL:
		case VALUE_REAL:
			//if (rhs.kind != VALUE_REAL){ error message }
			val += rhs;
			break;
		case VALUE_INT32:
			v32 += (int32) rhs;
			break;
		case VALUE_INT64:
			v64 += rhs;
			break;
		case VALUE_STRING:
			// if string then concatenate, if not then convert to string
			YYError( "unable to add a number to string" );
			break;
		} // end switch
		return *this;
	} // end operator+=
	YYRValue& operator+=( const char* rhs ) {
		switch( kind ) {
		case VALUE_BOOL:
		case VALUE_INT32:
		case VALUE_INT64:
		case VALUE_REAL:
			//if (rhs.kind != VALUE_REAL){ error message }
			YYError( "unable to add a string to a number" );
			break;
		case VALUE_STRING:
			{
				// if string then concatenate, if not then convert to string
				const char* pFirst = (pRefString != NULL) ? pRefString->get() : NULL;
				char* pNew = (char*)YYGML_AddString( pFirst, rhs );
				YYCreateString( this, pNew );
				YYFree(pNew);
			} // end block
			break;
		} // end switch
		return *this;
	} // end operator+=	
	friend YYRValue operator+( const YYRValue& lhs, const YYRValue& rhs ) {
		return YYRValue(lhs) += rhs;
	} // end operator+
	friend YYRValue operator+( const YYRValue& lhs, double rhs ) {
		return YYRValue(lhs) += rhs;
	} // end operator+
	friend YYRValue operator+( double lhs, const YYRValue& rhs ) {
		return YYRValue(lhs) += YYRValue(rhs);
	} // end operator+
	friend YYRValue operator+( const YYRValue& lhs, int rhs ) {
		return YYRValue(lhs) += rhs;
	} // end operator+
	friend YYRValue operator+( int lhs, const YYRValue& rhs ) {
		return YYRValue(lhs) += YYRValue(rhs);
	} // end operator+
	friend YYRValue operator+( const YYRValue& lhs, long long rhs ) {
		return YYRValue(lhs) += rhs;
	} // end operator+
	friend YYRValue operator+( long long lhs, const YYRValue& rhs ) {
		return YYRValue(lhs) += YYRValue(rhs);
	} // end operator+
	friend YYRValue operator+( const YYRValue& lhs, float rhs ) {
		return YYRValue(lhs) += rhs;
	} // end operator+
	friend YYRValue operator+( float lhs, const YYRValue& rhs ) {
		return YYRValue(lhs) += YYRValue(rhs);
	} // end operator+


	// --------------------------------------------------------------------------------------------------
	// operator - and -=
	// --------------------------------------------------------------------------------------------------
	YYRValue& operator-=( const YYRValue& rhs ) {
		switch( (kind & MASK_KIND_RVALUE) ) {
		case VALUE_BOOL:
		case VALUE_REAL:
			val -= REAL_RValue(&rhs);
			break;
		case VALUE_INT32:
			v32 -= INT32_RValue(&rhs);
			break;
		case VALUE_INT64:
			v64 -= INT64_RValue(&rhs);
			break;
		default:
			YYOpError( "-=", this, &rhs );
			break;
		} 
		return *this;
	} // end operator-=
	friend YYRValue operator-( const YYRValue& lhs, const YYRValue& rhs ) {
		return YYRValue(lhs) -= rhs;
	} // end operator-
	friend YYRValue operator-( const YYRValue& lhs, double rhs ) {
		return YYRValue(lhs) -= YYRValue(rhs);
	} // end operator-
	friend YYRValue operator-( double lhs, const YYRValue& rhs ) {
		return YYRValue(lhs) -= YYRValue(rhs);
	} // end operator-
	friend YYRValue operator-( const YYRValue& lhs, int rhs ) {
		return YYRValue(lhs) -= YYRValue(rhs);
	} // end operator-
	friend YYRValue operator-( int lhs, const YYRValue& rhs ) {
		return YYRValue(lhs) -= YYRValue(rhs);
	} // end operator-
	friend YYRValue operator-( const YYRValue& lhs, long long rhs ) {
		return YYRValue(lhs) -= YYRValue(rhs);
	} // end operator-
	friend YYRValue operator-( long long lhs, const YYRValue& rhs ) {
		return YYRValue(lhs) -= YYRValue(rhs);
	} // end operator-
	friend YYRValue operator-( const YYRValue& lhs, float rhs ) {
		return YYRValue(lhs) -= YYRValue(rhs);
	} // end operator-
	friend YYRValue operator-( float lhs, const YYRValue& rhs ) {
		return YYRValue(lhs) -= YYRValue(rhs);
	} // end operator-


	// --------------------------------------------------------------------------------------------------
	// operator / and /=
	// --------------------------------------------------------------------------------------------------
	YYRValue& operator/=( const YYRValue& rhs ) {
		switch ((kind & MASK_KIND_RVALUE)) {
		case VALUE_BOOL:
		case VALUE_REAL:
			val /= REAL_RValue(&rhs);
			break;
		case VALUE_INT32:
			switch ((rhs.kind & MASK_KIND_RVALUE)) {
			case VALUE_INT32:
				v32 /= rhs.v32;
				break;
			case VALUE_INT64:
				kind = VALUE_INT64;
				v64 /= rhs.v64;
				break;
			default:
				kind = VALUE_REAL;
				val = ((double)v32 / REAL_RValue(&rhs));
				break;
			} // end switch
			break;
		case VALUE_INT64:
			switch ((rhs.kind & MASK_KIND_RVALUE)) {
			case VALUE_INT32:
				v64 /= rhs.v32;
				break;
			case VALUE_INT64:
				v64 /= rhs.v64;
				break;
			default:
				kind = VALUE_REAL;
				val = ((double)v64 / REAL_RValue(&rhs));
				break;
			} // end switch
			break;
		default:
			YYOpError("/=", this, &rhs);
			break;
		}
		return *this;

	}
	friend YYRValue operator/( const YYRValue& lhs, const YYRValue& rhs ) {
		return YYRValue(lhs) /= rhs;
	} // end operator/
	friend YYRValue operator/( const YYRValue& lhs, double rhs ) {
		return YYRValue(lhs) /= YYRValue(rhs);
	} // end operator/
	friend YYRValue operator/( double lhs, const YYRValue& rhs ) {
		return YYRValue(lhs) /= YYRValue(rhs);
	} // end operator/
	friend YYRValue operator/( const YYRValue& lhs, int rhs ) {
		return YYRValue(lhs) /= YYRValue(rhs);
	} // end operator/
	friend YYRValue operator/( int lhs, const YYRValue& rhs ) {
		return YYRValue(lhs) /= YYRValue(rhs);
	} // end operator/
	friend YYRValue operator/( const YYRValue& lhs, long long rhs ) {
		return YYRValue(lhs) /= YYRValue(rhs);
	} // end operator/
	friend YYRValue operator/( long long lhs, const YYRValue& rhs ) {
		return YYRValue(lhs) /= YYRValue(rhs);
	} // end operator/
	friend YYRValue operator/( const YYRValue& lhs, float rhs ) {
		return YYRValue(lhs) /= YYRValue(rhs);
	} // end operator/
	friend YYRValue operator/( float lhs, const YYRValue& rhs ) {
		return YYRValue(lhs) /= YYRValue(rhs);
	} // end operator/

	// --------------------------------------------------------------------------------------------------
	// operator * and *=
	// --------------------------------------------------------------------------------------------------
	YYRValue& operator*=( const YYRValue& rhs ) {
		switch( (kind & MASK_KIND_RVALUE) ) {
		case VALUE_BOOL:
		case VALUE_REAL:
			val *= REAL_RValue(&rhs);
			break;
		case VALUE_INT32:
			switch ((rhs.kind & MASK_KIND_RVALUE)) {
			case VALUE_INT32:
				v32 *= rhs.v32;
				break;
			case VALUE_INT64:
				kind = VALUE_INT64;
				v64 *= rhs.v64;
				break;
			default:
				kind = VALUE_REAL;
				val = ((double)v32 * REAL_RValue(&rhs));
				break;
			} // end switch
			break;
		case VALUE_INT64:
			switch ((rhs.kind & MASK_KIND_RVALUE)) {
			case VALUE_INT32:
				v64 *= rhs.v32;
				break;
			case VALUE_INT64:
				v64 *= rhs.v64;
				break;
			default:
				kind = VALUE_REAL;
				val = ((double)v64 * REAL_RValue(&rhs));
				break;
			} // end switch
			break;
		default:
			YYOpError( "*=", this, &rhs );
			break;
		} 
		return *this;
	}
	friend YYRValue operator*( const YYRValue& lhs, const YYRValue& rhs ) {
		return YYRValue(lhs) *= rhs;
	} // end operator*
	friend YYRValue operator*( const YYRValue& lhs, double rhs ) {
		return YYRValue(lhs) *= YYRValue(rhs);
	} // end operator*
	friend YYRValue operator*( double lhs, const YYRValue& rhs ) {
		return YYRValue(lhs) *= YYRValue(rhs);
	} // end operator*
	friend YYRValue operator*( const YYRValue& lhs, int rhs ) {
		return YYRValue(lhs) *= YYRValue(rhs);
	} // end operator*
	friend YYRValue operator*( int lhs, const YYRValue& rhs ) {
		return YYRValue(lhs) *= YYRValue(rhs);
	} // end operator*
	friend YYRValue operator*( const YYRValue& lhs, long long rhs ) {
		return YYRValue(lhs) *= YYRValue(rhs);
	} // end operator*
	friend YYRValue operator*( long long lhs, const YYRValue& rhs ) {
		return YYRValue(lhs) *= YYRValue(rhs);
	} // end operator*
	friend YYRValue operator*( const YYRValue& lhs, float rhs ) {
		return YYRValue(lhs) *= YYRValue(rhs);
	} // end operator*
	friend YYRValue operator*( float lhs, const YYRValue& rhs ) {
		return YYRValue(lhs) *= YYRValue(rhs);
	} // end operator*

	// --------------------------------------------------------------------------------------------------
	// operator % and %=
	// --------------------------------------------------------------------------------------------------
	YYRValue& operator%=( const YYRValue& rhs ) {
		switch ((kind&MASK_KIND_RVALUE)) {
		case VALUE_BOOL:
		case VALUE_REAL:
			val = fmod(val, REAL_RValue(&rhs));
			break;
		case VALUE_INT32:
			switch ((rhs.kind & MASK_KIND_RVALUE)) {
			case VALUE_INT32:
				v32 %= rhs.v32;
				break;
			case VALUE_INT64:
				kind = VALUE_INT64;
				v64 %= rhs.v64;
				break;
			default:
				kind = VALUE_REAL;
				val = fmod((double)v32, REAL_RValue(&rhs));
				break;
			} // end switch
			break;
		case VALUE_INT64:
			switch ((rhs.kind & MASK_KIND_RVALUE)) {
			case VALUE_INT32:
				v64 %= rhs.v32;
				break;
			case VALUE_INT64:
				v64 %= rhs.v64;
				break;
			default:
				kind = VALUE_REAL;
				val = fmod((double)v64, REAL_RValue(&rhs));
				break;
			} // end switch
			break;
		default:
			YYOpError("%=", this, &rhs);
			break;
		} // end if
		return *this;
	}
	friend YYRValue operator%( const YYRValue& lhs, const YYRValue& rhs ) {
		return YYRValue(lhs) %= rhs;
	} // end operator%
	friend YYRValue operator%( const YYRValue& lhs, double rhs ) {
		return YYRValue(lhs) %= YYRValue(rhs);
	} // end operator%
	friend YYRValue operator%( double lhs, const YYRValue& rhs ) {
		return YYRValue(lhs) %= YYRValue(rhs);
	} // end operator%
	friend YYRValue operator%( const YYRValue& lhs, int rhs ) {
		return YYRValue(lhs) %= YYRValue(rhs);
	} // end operator%
	friend YYRValue operator%( int lhs, const YYRValue& rhs ) {
		return YYRValue(lhs) %= YYRValue(rhs);
	} // end operator%
	friend YYRValue operator%( const YYRValue& lhs, long long rhs ) {
		return YYRValue(lhs) %= YYRValue(rhs);
	} // end operator%
	friend YYRValue operator%( long long lhs, const YYRValue& rhs ) {
		return YYRValue(lhs) %= YYRValue(rhs);
	} // end operator%
	friend YYRValue operator%( const YYRValue& lhs, float rhs ) {
		return YYRValue(lhs) %= YYRValue(rhs);
	} // end operator%
	friend YYRValue operator%( float lhs, const YYRValue& rhs ) {
		return YYRValue(lhs) %= YYRValue(rhs);
	} // end operator%

	// --------------------------------------------------------------------------------------------------
	// operator [] (used for array rvalue dereference) and () - used for array lvalue dereference
	// --------------------------------------------------------------------------------------------------
	const YYRValue& operator[](const int _index)
	{
		const YYRValue* pV = NULL;
		if (((kind & MASK_KIND_RVALUE)== VALUE_ARRAY) && (pRefArray != NULL)) {
			//int ind1 = _index / VARIABLE_ARRAY_MAX_DIMENSION;
			//int ind2 = _index % VARIABLE_ARRAY_MAX_DIMENSION;
			ldiv_t ind = ldiv( _index, VARIABLE_ARRAY_MAX_DIMENSION);
			if (pRefArray->pOwner == NULL) pRefArray->pOwner = this;

			const DynamicArrayOfRValue* pArr = NULL;
			// find the entry
			if ((ind.quot >= 0) && (pRefArray != NULL) && (ind.quot < pRefArray->length)) {
				pArr = &pRefArray->pArray[ ind.quot ];
				if ((ind.rem >= 0) && (ind.rem < pArr->length)) {
					pV = (YYRValue*)&pArr->arr[ ind.rem ];
				} // end if
				else {
					YYError( "second index out of bounds request %d,%d maximum size is %d", ind.quot, ind.rem, pRefArray->length, pArr->length   );
				} // end else
			} // end if
			else {
				YYError( "first index out of bounds request %d maximum size is %d", ind.quot, (pRefArray != NULL) ? pRefArray->length : 0   );
			} // end else
		}
		/* else this is not an array */
		else {
			YYError( "trying to index variable that is not an array" );
			pV = this;
		} // end else
		return *pV;
	}

	YYRValue& operator()(const int _index)
	{
		return *(YYRValue*)ARRAY_LVAL_RValue(this, _index);
	}

	bool is_number() const
	{
		return (kind == VALUE_REAL || kind == VALUE_INT32 || kind == VALUE_INT64 || kind == VALUE_BOOL);
	}

	// --------------------------------------------------------------------------------------------------
	// operator ==
	// --------------------------------------------------------------------------------------------------
	friend bool operator==( const YYRValue& _v1, const YYRValue& _v2 ) {
		return (YYCompareVal( _v1, _v2, g_GMLMathEpsilon) == 0);
	}
	friend bool operator==( const YYRValue& _v1, double _v2 ) {
		return (
		   		_v1.is_number() && ((yyfabs(REAL_RValue(&_v1) - _v2) <= g_GMLMathEpsilon))
		   	   );
	}
	friend bool operator==( double _v1, const YYRValue& _v2 ) {
		return (
		   		_v2.is_number() && ((yyfabs(_v1 - REAL_RValue(&_v2)) <= g_GMLMathEpsilon))
		   	   );
	}
	friend bool operator==( const YYRValue& _v1, int _v2 ) {
		return (
		   		_v1.is_number() && ((yyfabs(REAL_RValue(&_v1) - _v2) <= g_GMLMathEpsilon))
		   	   );
	}
	friend bool operator==( int _v1, const YYRValue& _v2 ) {
		return (
		   		(_v2.is_number() && (yyfabs(_v1 - REAL_RValue(&_v2)) <= g_GMLMathEpsilon))
		   	   );
	}
	friend bool operator==( const YYRValue& _v1, long long _v2 ) {
		return (
		   		_v1.is_number() && (INT64_RValue(&_v1) == _v2)
		   	   );
	}
	friend bool operator==( long long _v1, const YYRValue& _v2 ) {
		return (
		   		_v2.is_number() && (_v1 ==  INT64_RValue(&_v2))
		   	   );
	}
	friend bool operator==( bool _v1, const YYRValue& _v2 ) {
		return (_v1 == (bool)_v2);
	}
	friend bool operator==( const YYRValue& _v1, bool _v2 ) {
		return ((bool)_v1 == _v2);
	}
	
	// --------------------------------------------------------------------------------------------------
	// operator !=
	// --------------------------------------------------------------------------------------------------
	friend bool operator!=( const YYRValue& _v1, const YYRValue& _v2 ) {
		return !(_v1 == _v2);
	}
	friend bool operator!=( const YYRValue& _v1, double _v2 ) {
		return !(_v1 == _v2);
	}
	friend bool operator!=( double _v1, const YYRValue& _v2 ) {
		return !(_v1 == _v2);
	}
	friend bool operator!=( const YYRValue& _v1, int _v2 ) {
		return !(_v1 == _v2);
	}
	friend bool operator!=( int _v1, const YYRValue& _v2 ) {
		return !(_v1 == _v2);
	}
	friend bool operator!=( const YYRValue& _v1, long long _v2 ) {
		return !(_v1 == _v2);
	}
	friend bool operator!=( long long _v1, const YYRValue& _v2 ) {
		return !(_v1 == _v2);
	}
	friend bool operator!=( bool _v1, const YYRValue& _v2 ) {
		return !(_v1 == (bool)_v2);
	}
	friend bool operator!=( const YYRValue& _v1, bool _v2 ) {
		return !((bool)_v1 == _v2);
	}

	// --------------------------------------------------------------------------------------------------
	// operator <
	// --------------------------------------------------------------------------------------------------
	friend bool operator<( const YYRValue& _v1, const YYRValue& _v2) {
		return (YYCompareVal( _v1, _v2, g_GMLMathEpsilon) < 0);
	} // end 
	friend bool operator<( const YYRValue& _v1, double _v2) {
		return (
		   		((REAL_RValue(&_v1) - _v2) < -g_GMLMathEpsilon)
		   	   );
	} // end 
	friend bool operator<( double _v1, const YYRValue& _v2) {
		return (
		   		((_v1 - REAL_RValue(&_v2)) < -g_GMLMathEpsilon)
		   	   );
	} // end 
	friend bool operator<( const YYRValue& _v1, int _v2) {
		return (
		   		((REAL_RValue(&_v1) - _v2) < -g_GMLMathEpsilon)
		   	   );
	} // end 
	friend bool operator<( int _v1, const YYRValue& _v2) {
		return (
		   		((_v1 - REAL_RValue(&_v2)) < -g_GMLMathEpsilon)
		   	   );
	} // end 
	friend bool operator<( const YYRValue& _v1, long long _v2) {
		return (
		   		(INT64_RValue(&_v1) < _v2)
		   	   );
	} // end 
	friend bool operator<( long long _v1, const YYRValue& _v2) {
		return (
		   		(_v1 < INT64_RValue(&_v2))
		   	   );
	} // end 


	// --------------------------------------------------------------------------------------------------
	// operator <=
	// --------------------------------------------------------------------------------------------------
	friend bool operator<=( const YYRValue& _v1, const YYRValue& _v2) {
		return (YYCompareVal( _v1, _v2, g_GMLMathEpsilon) <= 0);
	} // end 
	friend bool operator<=( const YYRValue& _v1, double _v2) {
		return (
		   		((REAL_RValue(&_v1) - _v2) <= g_GMLMathEpsilon)
		   	   );
	} // end 
	friend bool operator<=( double _v1, const YYRValue& _v2) {
		return (
		   		((_v1 - REAL_RValue(&_v2)) <= g_GMLMathEpsilon)
		   	   );
	} // end 
	friend bool operator<=( const YYRValue& _v1, int _v2) {
		return (
		   		((REAL_RValue(&_v1) - _v2) <= g_GMLMathEpsilon)
		   	   );
	} // end 
	friend bool operator<=( int _v1, const YYRValue& _v2) {
		return (
		   		((_v1 - REAL_RValue(&_v2)) <= g_GMLMathEpsilon)
		   	   );
	} // end 
	friend bool operator<=( const YYRValue& _v1, long long _v2) {
		return (
		   		(INT64_RValue(&_v1) <= _v2)
		   	   );
	} // end 
	friend bool operator<=( long long _v1, const YYRValue& _v2) {
		return (
		   		(_v1 <= INT64_RValue(&_v2))
		   	   );
	} // end 

	// --------------------------------------------------------------------------------------------------
	// operator >
	// --------------------------------------------------------------------------------------------------
	friend bool operator>( const YYRValue& _v1, const YYRValue& _v2) {
		return (YYCompareVal( _v1, _v2, g_GMLMathEpsilon) > 0);
	} // end 
	friend bool operator>( const YYRValue& _v1, double _v2) {
		return (
		   		((REAL_RValue(&_v1) - _v2) > g_GMLMathEpsilon)
		   	   );
	} // end 
	friend bool operator>( double _v1, const YYRValue& _v2) {
		return (
		   		((_v1 - REAL_RValue(&_v2)) > g_GMLMathEpsilon)
		   	   );
	} // end 
	friend bool operator>( const YYRValue& _v1, int _v2) {
		return (
		   		((REAL_RValue(&_v1) - _v2) > g_GMLMathEpsilon)
		   	   );
	} // end 
	friend bool operator>( int _v1, const YYRValue& _v2) {
		return (
		   		((_v1 - REAL_RValue(&_v2)) > g_GMLMathEpsilon)
		   	   );
	} // end 
	friend bool operator>( const YYRValue& _v1, long long _v2) {
		return (
		   		(INT64_RValue(&_v1) > _v2)
		   	   );
	} // end 
	friend bool operator>( long long _v1, const YYRValue& _v2) {
		return (
		   		(_v1 > INT64_RValue(&_v2))
		   	   );
	} // end 


	// --------------------------------------------------------------------------------------------------
	// operator >=
	// --------------------------------------------------------------------------------------------------
	friend bool operator>=( const YYRValue& _v1, const YYRValue& _v2) {
		return (YYCompareVal( _v1, _v2, g_GMLMathEpsilon) >= 0);
	} // end 
	friend bool operator>=( const YYRValue& _v1, double _v2) {
		return (
		   		((REAL_RValue(&_v1) - _v2) >= -g_GMLMathEpsilon)
		   	   );
	} // end 
	friend bool operator>=( double _v1, const YYRValue& _v2) {
		return (
		   		((_v1 - REAL_RValue(&_v2)) >= -g_GMLMathEpsilon)
		   	   );
	} // end 
	friend bool operator>=( const YYRValue& _v1, int _v2) {
		return (
		   		((REAL_RValue(&_v1) - _v2) >= -g_GMLMathEpsilon)
		   	   );
	} // end 
	friend bool operator>=( int _v1, const YYRValue& _v2) {
		return (
		   		((_v1 - REAL_RValue(&_v2)) >= -g_GMLMathEpsilon)
		   	   );
	} // end 
	friend bool operator>=( const YYRValue& _v1, long long _v2) {
		return (
		   		(INT64_RValue(&_v1) >= _v2)
		   	   );
	} // end 
	friend bool operator>=( long long _v1, const YYRValue& _v2) {
		return (
		   		(_v1 >= INT64_RValue(&_v2))
		   	   );
	} // end 

	// --------------------------------------------------------------------------------------------------
	// operator |=
	// --------------------------------------------------------------------------------------------------
	YYRValue& operator|=( const YYRValue& _rhs ) {
		switch( (kind & MASK_KIND_RVALUE) ) {
		case VALUE_BOOL:
		case VALUE_REAL:
			{
				long long v = (long long)val;
				v |= INT64_RValue(&_rhs);
				val = (double)v;
			} // end block
			break;
		case VALUE_INT32:
			v32 |= INT32_RValue(&_rhs);
			break;
		case VALUE_INT64:
			v64 |= INT64_RValue(&_rhs);
			break;
		default:
			YYOpError( "|=", this, &_rhs );
			break;
		} 
		return *this;
	}
	YYRValue& operator|=( double _rhs ) {
		switch( (kind & MASK_KIND_RVALUE) ) {
		case VALUE_BOOL:
		case VALUE_REAL:
			{
				long long v = (long long)val;
				v |= (long long)_rhs;
				val = (double)v;
			} // end block
			break;
		case VALUE_INT32:
			v32 |= (int)_rhs;
			break;
		case VALUE_INT64:
			v64 |= (long long)_rhs;
			break;
		default:
			{
				YYRValue rhs(_rhs);
				YYOpError( "|=", this, &rhs );
			} // end block
			break;
		} 
		return *this;
	}
	YYRValue& operator|=( long long _rhs ) {
		switch( (kind & MASK_KIND_RVALUE) ) {
		case VALUE_BOOL:
		case VALUE_REAL:
			{
				long long v = (long long)val;
				v |= _rhs;
				val = (double)v;
			} // end block
			break;
		case VALUE_INT32:
			v32 |= (int)_rhs;
			break;
		case VALUE_INT64:
			v64 |= _rhs;
			break;
		default:
			{
				YYRValue rhs(_rhs);
				YYOpError( "|=", this, &rhs );
			} // end block
			break;
		} 
		return *this;
	}
	YYRValue& operator|=( int _rhs ) {
		switch( (kind & MASK_KIND_RVALUE) ) {
		case VALUE_BOOL:
		case VALUE_REAL:
			{
				long long v = (long long)val;
				v |= (long long)_rhs;
				val = (double)v;
			} // end block
			break;
		case VALUE_INT32:
			v32 |= (int)_rhs;
			break;
		case VALUE_INT64:
			v64 |= (long long)_rhs;
			break;
		default:
			{
				YYRValue rhs(_rhs);
				YYOpError( "|=", this, &rhs );
			} // end block
			break;
		} 
		return *this;		
	}

	// --------------------------------------------------------------------------------------------------
	// operator &=
	// --------------------------------------------------------------------------------------------------
	YYRValue& operator&=( const YYRValue& _rhs ) {
		switch( (kind & MASK_KIND_RVALUE) ) {
		case VALUE_BOOL:
		case VALUE_REAL:
			{
				long long v = (long long)val;
				v &= INT64_RValue(&_rhs);
				val = (double)v;
			} // end block 
			break;
		case VALUE_INT32:
			v32 &= INT32_RValue(&_rhs);
			break;
		case VALUE_INT64:
			v64 &= INT64_RValue(&_rhs);
			break;
		default:
			YYOpError( "&=", this, &_rhs );
			break;
		} 
		return *this;
	}
	YYRValue& operator&=( double _rhs ) {
		switch( (kind & MASK_KIND_RVALUE) ) {
		case VALUE_BOOL:
		case VALUE_REAL:
			{
				long long v = (long long)val;
				v &= (long long)_rhs;
				val = (double)v;
			} // end block
			break;
		case VALUE_INT32:
			v32 &= (int)_rhs;
			break;
		case VALUE_INT64:
			v64 &= (long long)_rhs;
			break;
		default:
			{
				YYRValue rhs(_rhs);
				YYOpError( "&=", this, &rhs );
			} // end block
			break;
		} 
		return *this;
	}
	YYRValue& operator&=( long long _rhs ) {
		switch( (kind & MASK_KIND_RVALUE) ) {
		case VALUE_BOOL:
		case VALUE_REAL:
			{
				long long v = (long long)val;
				v &= _rhs;
				val = (double)v;
			} // end block
			break;
		case VALUE_INT32:
			v32 &= (int)_rhs;
			break;
		case VALUE_INT64:
			v64 &= _rhs;
			break;
		default:
			{
				YYRValue rhs(_rhs);
				YYOpError( "&=", this, &rhs );
			} // end block
			break;
		} 
		return *this;
	}
	YYRValue& operator&=( int _rhs ) {
		switch( (kind & MASK_KIND_RVALUE) ) {
		case VALUE_BOOL:
		case VALUE_REAL:
			{
				long long v = (long long)val;
				v &= (long long)_rhs;
				val = (double)v;
			} // end block
			break;
		case VALUE_INT32:
			v32 &= (int)_rhs;
			break;
		case VALUE_INT64:
			v64 &= (long long)_rhs;
			break;
		default:
			{
				YYRValue rhs(_rhs);
				YYOpError( "&=", this, &rhs );
			} // end block
			break;
		} 
		return *this;		
	}

	// --------------------------------------------------------------------------------------------------
	// operator ^=
	// --------------------------------------------------------------------------------------------------
	YYRValue& operator^=( const YYRValue& _rhs ) {
		switch( (kind & MASK_KIND_RVALUE) ) {
		case VALUE_BOOL:
		case VALUE_REAL:
			{
				long long v = (long long)val;
				v ^= INT64_RValue(&_rhs);
				val = (double)v;
			} // end block
			break;
		case VALUE_INT32:
			v32 ^= INT32_RValue(&_rhs);
			break;
		case VALUE_INT64:
			v64 ^= INT64_RValue(&_rhs);
			break;
		default:
			YYOpError( "^=", this, &_rhs );
			break;
		} 
		return *this;
	}
	YYRValue& operator^=( double _rhs ) {
		switch( (kind & MASK_KIND_RVALUE) ) {
		case VALUE_BOOL:
		case VALUE_REAL:
			{
				long long v = (long long)val;
				v ^= (long long)_rhs;
				val = (double)v;
			} // end block
			break;
		case VALUE_INT32:
			v32 ^= (int)_rhs;
			break;
		case VALUE_INT64:
			v64 ^= (long long)_rhs;
			break;
		default:
			{
				YYRValue rhs(_rhs);
				YYOpError( "^=", this, &rhs );
			} // end block
			break;
		} 
		return *this;
	}
	YYRValue& operator^=( long long _rhs ) {
		switch( (kind & MASK_KIND_RVALUE) ) {
		case VALUE_BOOL:
		case VALUE_REAL:
			{
				long long v = (long long)val;
				v ^= _rhs;
				val = (double)v;
			} //end block
			break;
		case VALUE_INT32:
			v32 ^= (int)_rhs;
			break;
		case VALUE_INT64:
			v64 ^= _rhs;
			break;
		default:
			{
				YYRValue rhs(_rhs);
				YYOpError( "^=", this, &rhs );
			} // end block
			break;
		} 
		return *this;
	}
	YYRValue& operator^=( int _rhs ) {
		switch( (kind & MASK_KIND_RVALUE) ) {
		case VALUE_BOOL:
		case VALUE_REAL:
			{
				long long v = (long long)val;
				v ^= (long long)_rhs;
				val = (double)v;
			} // end block
			break;
		case VALUE_INT32:
			v32 ^= (int)_rhs;
			break;
		case VALUE_INT64:
			v64 ^= (long long)_rhs;
			break;
		default:
			{
				YYRValue rhs(_rhs);
				YYOpError( "^=", this, &rhs );
			} // end block
			break;
		} 
		return *this;		
	}

};

#else
struct YYRValue : RValue
{
};
#endif

typedef	void (*PFUNC_YYGML)( CInstance* _pSelf, CInstance* _pOther );
struct YYGMLFuncs
{
	const char* pName;
	PFUNC_YYGML pFunc;
};

//extern YYGMLFuncs g_GMLFuncs[];
extern YYObjectBase* g_pGlobal;

#if defined(YYLLVM)
inline char* YYGML_string( double val ) { 
	YYRValue a(val); 
	return YYGML_string( a ); 
}
inline char* YYGML_string( int val ) { 
	YYRValue a(val); 
	return YYGML_string( a ); 
}
inline char* YYGML_string( const char* str ) { return (char*)str; }

inline char* YYGML_AddString( const YYRValue& _val, const char* _str ) { const char* pS = (_val.pRefString != NULL) ? _val.pRefString->get() : NULL; return YYGML_AddString( pS, _str ); }
inline char* YYGML_AddString( const char* _str, const YYRValue& _val ) { const char* pS = (_val.pRefString != NULL) ? _val.pRefString->get() : NULL; return YYGML_AddString( _str, pS ); }
inline char* YYGML_AddString( const YYRValue& _val1, const YYRValue& _val2 ) { 
	const char* pF = (_val1.pRefString != NULL) ? _val1.pRefString->get() : NULL; 
	const char* pS = (_val2.pRefString != NULL) ? _val2.pRefString->get() : NULL; 
	return YYGML_AddString( pF, pS ); 
}
inline int strcmp( const YYRValue& _val1, const char* _str )	{ return (
	                                                                      (((_val1.kind&MASK_KIND_RVALUE)==VALUE_STRING) && ((_val1.pRefString == NULL)||(_val1.pRefString->get() == NULL)) && (_str != NULL) && (*_str == '\0')) ||  
																		  (((_val1.kind&MASK_KIND_RVALUE)==VALUE_STRING) && (_val1.pRefString != NULL) && (_str != NULL) && (_val1.pRefString->get() != NULL) && (strcmp( _val1.pRefString->get(), _str )==0))
																		  ) ? 0 : 1 ; }
inline int strcmp( const char* _str, const YYRValue& _val1 )	{ return (
																		  (((_val1.kind&MASK_KIND_RVALUE)==VALUE_STRING) && ((_val1.pRefString == NULL)||(_val1.pRefString->get() == NULL)) && (_str != NULL) && (*_str == '\0')) ||  
																		  (((_val1.kind&MASK_KIND_RVALUE)==VALUE_STRING) && (_val1.pRefString != NULL) &&  (_str != NULL) && (_val1.pRefString->get() != NULL) && (strcmp( _str, _val1.pRefString->get() )==0))
																		  ) ? 0 : 1 ; }
#endif

class YYStrBuilder
{
public:
	char*	m_pBuffer;
	int		m_len;
	int		m_curr;

	YYStrBuilder() {
		m_pBuffer = NULL;
		m_len = 0;
		m_curr = 0;
	} // end constructor

	~YYStrBuilder() {
		if (m_pBuffer != NULL) {
			YYFree( m_pBuffer );
			m_pBuffer = NULL;
			m_len = 0;
			m_curr = 0;
		} // end if
	} // end destructor

	char* buffer(void)
	{
		char* pRet = (m_curr == 0) ? (char*)&m_curr : m_pBuffer;
		return pRet;
	} // end answer

	char* answer( void )
	{
		char* pRet = buffer();
		m_curr = 0;
		return pRet;
	} // end answer

	void reset(void)
	{
		m_curr = 0;
	} // end reset

	char* ensureSpace( int _size )
	{
		// check to see if we have enough space
		int freeSpace = (m_len - (m_curr+1));
		if (_size > freeSpace) {

			// not enough space so grow the amount of memory we have
			int oldSize = (m_len == 0) ? _size : m_len;
			int newSize = (oldSize * 3) / 2;
			if (newSize < (m_curr + _size)) {
				newSize = ((m_curr + _size) * 3)/2;
			} // end if

			// allocate the new space
			char* pOldBuffer = m_pBuffer;
			m_pBuffer = (char*)YYAlloc( newSize );

			// copy over the old buffer to the new one
			memcpy( m_pBuffer, pOldBuffer, m_len );
			m_len = newSize;

			// free the old buffer
			if (pOldBuffer != NULL) {
				YYFree( pOldBuffer );
			} // end if
		} // end if


		return m_pBuffer + m_curr;
	} // end ensureSpace

	YYStrBuilder& operator<<( const char* _pStr )
	{
		if (_pStr != NULL) {

			int len = (int)strlen(_pStr)+1;
			char* pCopy = ensureSpace( len );
			strcpy( pCopy, _pStr);
			m_curr += len-1;

		} // endif
		return *this;
	} // end constant string

	YYStrBuilder& operator<<( const YYRValue& _val )
	{
		int maxLen = 256;
		char* pBase = (char*)YYAlloc( maxLen );
		char* pCurrent = pBase;
		*pBase = '\0';
		STRING_RValue( &pCurrent, &pBase, &maxLen, &_val );
		int len = (int)((pCurrent - pBase)+1);
		char* pCopy = ensureSpace(len);
		strcpy( pCopy, pBase );
		m_curr+= len-1;
		YYFree( pBase );
		return *this;
	} // end YYRValue

	YYStrBuilder& operator<<( int _n ) 
	{
		char a[256];
		yyitoa( _n, a, 10 );
		int len = (int)strlen( a );
		char* pCopy = ensureSpace( len+1 );
		strcpy( pCopy, a);
		m_curr += len;
		return *this;
	} // end integer

	YYStrBuilder& operator<<(char _c)
	{
		char* pCopy = ensureSpace(2);
		*pCopy++ = _c;
		*pCopy = '\0';
		++m_curr;
		return *this;
	} // end char
#if defined(YYLLVM)
	YYStrBuilder& operator<<( double _d ) 
	{
		YYRValue a(_d);
		return *this << a;
	} // end integer
#endif
};

#if defined(YYLLVM)
inline void YYOpError( const char* pOp, const YYRValue* _lhs, const YYRValue* _rhs ) 
{
	YYStrBuilder sbLHS, sbRHS;
	sbLHS << *_lhs;
	sbRHS << *_rhs;
	YYError( "invalid type for %s lhs=%s (type=%d), rhs=%s (type=%d)", pOp, sbLHS.answer(), (_lhs->kind & MASK_KIND_RVALUE), sbRHS.answer(), (_rhs->kind & MASK_KIND_RVALUE) );
}
#endif

struct YYLocalArgs
{
	int m_count;
	YYRValue** m_args;
	YYLocalArgs( int _count, YYRValue** _args, YYRValue* _newarguments, YYRValue** _newargs ) {
		m_count = _count;
		m_args = _newargs;
		for( int n=0; n<m_count ; ++n) {
			COPY_RValue__Post( &_newarguments[n], _args[n]);
			m_args[n] = &_newarguments[n];
		} // end for
	} // end YYLocalArgs

	~YYLocalArgs() {
		for( int n=0; n<m_count ; ++n) {
			FREE_RValue(m_args[n]);
		} // end for
		
	} // end YYLocalArgs
};




//extern char* g_pWAD;
//extern int g_nWADFileLength;
extern int g_nGlobalVariables;
extern int g_nInstanceVariables;
//extern int g_nYYCode;
//extern YYVAR** g_ppVars;
//extern YYVAR** g_ppFuncs;
//extern YYGMLFuncs* g_pGMLFuncs;

// Structure for passing information out of a dynamic library when using DLL's (see WinPhone, Win8Native)
struct SLLVMVars {
	char*			pWad;				// pointer to the Wad
	int				nWadFileLength;		// the length of the wad
	int				nGlobalVariables;	// global varables
	int				nInstanceVariables;	// instance variables
	int				nYYCode;
	YYVAR**			ppVars;
	YYVAR**			ppFuncs;
	YYGMLFuncs*		pGMLFuncs;
	void*			pYYStackTrace;		// pointer to the stack trace
};
extern SLLVMVars*	g_pLLVMVars;
typedef void (*PFUNC_InitYYC)( SLLVMVars* _pVars );

#if !defined(WIN_UAP)
YYCEXPORT void InitLLVM( SLLVMVars* _pVars );
#endif


enum eLLVMVars
{
	eYYVar_Global,
	eYYVar_MathEpsilon,
	eYYVar_YYStackTrace
};

typedef void (*PFUNC_SetLLVMVar)( eLLVMVars _key, const YYRValue* _pVal );
extern PFUNC_SetLLVMVar	g_pSetLLVMVar;



#endif
