#ifndef __YYSLOT_H__
#define __YYSLOT_H__

#include "YYStd.h"

template <typename T > struct YYSlot
{
	T**			m_ppEntries;
	int			m_nCapacity;
	int			m_nCount;
	int			m_lastFreed;

	YYSlot( int _size ) {
		m_ppEntries = (T**)YYAlloc( _size*sizeof(T*) );
		memset( m_ppEntries, 0, sizeof(T*)*_size );
		m_nCapacity = _size;
		m_nCount = 0;
		m_lastFreed = 0;
	} // end YYSlot

	~YYSlot() {
		YYFree( m_ppEntries );
		m_ppEntries = NULL;
		m_nCapacity = 0;
		m_nCount = 0;
		m_lastFreed = 0;
	} // end destructor

	int allocSlot( T* _pEntry ) {
		// check to see if we should increase capacity
		if (m_nCount >= m_nCapacity) {
			int newsize = (m_nCapacity * 3)/2;
			m_ppEntries = (T**)YYRealloc( m_ppEntries, newsize*sizeof(T*) );
			memset( &m_ppEntries[m_nCapacity], 0, sizeof(T*)*(newsize - m_nCapacity) );
			m_nCapacity = newsize;
		} // end if

		// find a free slot
		int nRet = -1;
		for( int n=m_lastFreed, num=m_nCapacity; num>0; --num, ++n) {
			if (n>=m_nCapacity) n=0;
			if (m_ppEntries[n] == NULL) {
				m_ppEntries[n] = _pEntry;
				nRet = n;
				m_lastFreed = n;
				break;
			} // end if
		} // end for

		++m_nCount;
		return nRet;
	} // end AllocSlot

	void freeSlot( int _slot ) {
		m_ppEntries[ _slot ] = NULL;
		//m_lastFreed = _slot;
		m_lastFreed = yymin(_slot, m_lastFreed);	// store the earliest slot freed so we fill up earlier slots first
		--m_nCount;
	} // end FreeSlot

	T* getEntry( int _n ) const { return ((_n >= 0) && (_n < m_nCapacity)) ? m_ppEntries[ _n ] : NULL; }
	int capacity( void ) const { return m_nCapacity; }
	int count( void ) const { return m_nCount; }
}; // end template



#endif