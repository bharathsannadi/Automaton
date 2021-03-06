package com.athaydes.automaton

import com.athaydes.automaton.selector.AutomatonSelector

/**
 * @author Renato
 */
abstract class HasSelectors<K, A extends Automaton> extends Automaton<A> {

	Map<String, AutomatonSelector<K>> selectors

	abstract K getRoot()

	protected List ensurePrefixed( String selector ) {
		def prefixes = selectors.keySet()
		def prefix = prefixes.find { selector.startsWith it }
		[ prefix ?: prefixes[ 0 ], prefix ? selector - prefix : selector ]
	}

	protected K findOnePrefixed( String prefix, String query ) {
		def target = findAllPrefixed( prefix, query, 1 )
		if ( target ) target.first() else
			throw new GuiItemNotFound( "Could not locate prefix=$prefix, query=$query" )
	}

	protected List<K> findAllPrefixed( String prefix, String query, int limit = Integer.MAX_VALUE ) {
		def automatonSelector = selectors[ prefix ]
		automatonSelector.apply( prefix, query, root, limit )
	}

	K getAt( String selector ) {
		findOnePrefixed( ensurePrefixed( selector ) )
	}

	def <T> T getAt( Class<T> type ) {
		findOnePrefixed( 'type:', type.simpleName ) as T
	}

	List<K> getAll( String selector, int limit = Integer.MAX_VALUE ) {
		def (prefix, query) = ensurePrefixed( selector )
		findAllPrefixed( prefix, query, limit )
	}

	def <T> List<? extends T> getAll( Class<T> cls, int limit = Integer.MAX_VALUE ) {
		findAllPrefixed( 'type:', cls.simpleName, limit ) as List<T>
	}

}
