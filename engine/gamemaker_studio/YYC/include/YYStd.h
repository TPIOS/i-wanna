#ifndef __YYSTD_H__
#define __YYSTD_H__

#if defined(YYLLVM) && defined(YYLLVM_SEP_DLL)
#define YYCEXTERN __declspec( dllimport )
#define YYCEXPORT extern "C" __declspec( dllexport )
#elif defined(__YYLLVM__) && defined(YYLLVM_SEP_DLL)
#define YYCEXTERN __declspec( dllexport )
#define YYCEXPORT 
#else
#define YYCEXTERN 
#define YYCEXPORT 
#endif

#if defined(YYPSVITA)
#include <alloca.h>
#endif

#if defined(YYPS4)
#define FORCEINLINE inline
#define FORCEINLINE_ATTR __attribute__((always_inline))
#define FORCEOPTIMIZE_OFF_START		
#define FORCEOPTIMIZE_OFF_END		
#define OPTIMIZE_OFF_ATTR			__attribute__((optnone))
#include <stdlib.h>
#elif defined(__GNUC__)
#define FORCEINLINE inline
#define FORCEINLINE_ATTR __attribute__((always_inline))
#define FORCEOPTIMIZE_OFF_START		
#define FORCEOPTIMIZE_OFF_END		
#define OPTIMIZE_OFF_ATTR			__attribute__((optnone))
#include <stdlib.h>
#ifndef alloca
#include <alloca.h>
#endif
#elif defined(__clang__) && defined(_MSC_VER)
#define FORCEINLINE __forceinline
#define FORCEINLINE_ATTR 
#define FORCEOPTIMIZE_OFF_START		
#define FORCEOPTIMIZE_OFF_END		
#define OPTIMIZE_OFF_ATTR 			__attribute__((optnone))
#include <malloc.h>
#elif	defined(_MSC_VER)
#define FORCEINLINE __forceinline
#define FORCEINLINE_ATTR 
#define FORCEOPTIMIZE_OFF_START		__pragma(optimize("", off))
#define FORCEOPTIMIZE_OFF_END		__pragma(optimize("", on))
#define OPTIMIZE_OFF_ATTR 
#include <malloc.h>
#ifndef alloca
#define alloca				_alloca
#endif
#else
#define FORCEINLINE inline
#define FORCEINLINE_ATTR 
#define FORCEOPTIMIZE_OFF_START
#define FORCEOPTIMIZE_OFF_END
#define OPTIMIZE_OFF_ATTR 
#endif




#ifndef __YOYO_TYPES_H__
typedef		int		int32;
typedef		long long int64;
#endif


#ifndef _CRT_SECURE_NO_WARNINGS
#define _CRT_SECURE_NO_WARNINGS
#endif

struct RValue;

YYCEXTERN void* YYAlloc( int _size );
YYCEXTERN void* YYRealloc( void* _p, int _size );
YYCEXTERN void YYFree( const void* _p );
YYCEXTERN const char* YYStrDup( const char* _pS );

YYCEXTERN int YYstrnlen( const char* _pS, int n );
YYCEXTERN void YYCreateString( RValue* _pVal, const char* _pS );
YYCEXTERN void YYSetString( RValue* _pVal, const char* _pS );
YYCEXTERN void YYStrFree( const char* _pS );


#endif