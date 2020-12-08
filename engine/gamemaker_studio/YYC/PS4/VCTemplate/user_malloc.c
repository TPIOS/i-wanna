/* SCE CONFIDENTIAL
 PlayStation(R)4 Programmer Tool Runtime Library Release 01.750.061
 * Copyright (C) 2013 Sony Computer Entertainment Inc.
 * All Rights Reserved.
 */


#include <stdlib.h>
#include <mspace.h>
#include <kernel.h>

#define HEAP_SIZE (1 * 1024 * 1024 * 1024)

static SceLibcMspace s_mspace;
static off_t s_memStart;
static size_t s_memLength = 1 * 1024 * 1024 * 1024;
static size_t s_memAlign = 2 * 1024 * 1024;

int user_malloc_init(void);
int user_malloc_finalize(void);
void *user_malloc(size_t size);
void user_free(void *ptr);
void *user_calloc(size_t nelem, size_t size);
void *user_realloc(void *ptr, size_t size);
void *user_memalign(size_t boundary, size_t size);
int user_posix_memalign(void **ptr, size_t boundary, size_t size);
void *user_reallocalign(void *ptr, size_t size, size_t boundary);
int user_malloc_stats(SceLibcMallocManagedSize *mmsize);
int user_malloc_stats_fast(SceLibcMallocManagedSize *mmsize);
size_t user_malloc_usable_size(void *ptr);

//E Replace _malloc_init function.
//J _malloc_init 関数と置き換わる
int user_malloc_init(void)
{
	int res;
	void *addr;
	
	//E Allocate direct memory
	//J ダイレクトメモリを割り当てる
	res = sceKernelAllocateDirectMemory(0, SCE_KERNEL_MAIN_DMEM_SIZE, s_memLength, s_memAlign, SCE_KERNEL_WB_ONION, &s_memStart);
	if (res < 0) {
		//E Error handling
		//J エラー処理
		return 1;
	}

	addr = NULL;
	//E Map direct memory to the process address space
	//J ダイレクトメモリをプロセスアドレス空間にマップする
	res = sceKernelMapDirectMemory(&addr, HEAP_SIZE, SCE_KERNEL_PROT_CPU_READ | SCE_KERNEL_PROT_CPU_WRITE, 0, s_memStart, s_memAlign);
	if (res < 0) {
		//E Error handling
		//J エラー処理
		return 1;
	}

	//E Generate mspace
	//J mspace を生成する
	s_mspace = sceLibcMspaceCreate("User Malloc", addr, HEAP_SIZE, 0);
	if (s_mspace == NULL) {
		//E Error handling
		//J エラー処理
		return 1;
	}

	return 0;
}

//E Replace _malloc_finalize function.
//J _malloc_finalize 関数と置き換わる
int user_malloc_finalize(void)
{
	int res;

	if (s_mspace != NULL) {
		//E Free mspace
		//J mspace を解放する
		res = sceLibcMspaceDestroy(s_mspace);
		if (res != 0) {
			//E Error handling
			//J エラー処理
			return 1;
		}
	}

	//E Release direct memory
	//J ダイレクトメモリを解放する
	res = sceKernelReleaseDirectMemory(s_memStart, s_memLength);
	if (res < 0) {
		//E Error handling
		//J エラー処理
		return 1;
	}

	return 0;
}

//E Replace malloc function.
//J malloc 関数と置き換わる
void *user_malloc(size_t size)
{
	void *ret = sceLibcMspaceMalloc(s_mspace, size);
	//if(ret == NULL)
		//YYError("Memory allocation error attempting to allocate %d bytes\n",size);
	return ret;
}

//E Replace free function.
//J free 関数と置き換わる
void user_free(void *ptr)
{
	sceLibcMspaceFree(s_mspace, ptr);
}

//E Replace calloc function.
//J calloc 関数と置き換わる
void *user_calloc(size_t nelem, size_t size)
{
	return sceLibcMspaceCalloc(s_mspace, nelem, size);
}

//E Replace realloc function.
//J realloc 関数と置き換わる
void *user_realloc(void *ptr, size_t size)
{
	return sceLibcMspaceRealloc(s_mspace, ptr, size);
}

//E Replace memalign function.
//J memalign 関数と置き換わる
void *user_memalign(size_t boundary, size_t size)
{
	return sceLibcMspaceMemalign(s_mspace, boundary, size);
}

//E Replace posix_memalign function.
//J posix_memalign 関数と置き換わる
int user_posix_memalign(void **ptr, size_t boundary, size_t size)
{
	return sceLibcMspacePosixMemalign(s_mspace, ptr, boundary, size);
}

//E Replace reallocalign function.
//J reallocalign 関数と置き換わる
void *user_reallocalign(void *ptr, size_t size, size_t boundary)
{
	return sceLibcMspaceReallocalign(s_mspace, ptr, boundary, size);
}

//E Replace malloc_stats function.
//J malloc_stats 関数と置き換わる
int user_malloc_stats(SceLibcMallocManagedSize *mmsize)
{
	return sceLibcMspaceMallocStats(s_mspace, mmsize);
}

//E Replace malloc_stats_fast function.
//J malloc_stata_fast 関数と置き換わる
int user_malloc_stats_fast(SceLibcMallocManagedSize *mmsize)
{
	return sceLibcMspaceMallocStatsFast(s_mspace, mmsize);
}

//E Replace malloc_usable_size function.
//J malloc_usable_size 関数と置き換わる
size_t user_malloc_usable_size(void *ptr)
{
	return sceLibcMspaceMallocUsableSize(ptr);
}