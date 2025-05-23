package com.kdk.config.mvc;

import org.apache.commons.text.StringEscapeUtils;

import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.core.io.SerializedString;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2024. 6. 8. 김대광	최초작성
 * </pre>
 *
 *
 * @author 김대광
 */
public class HTMLCharacterEscapes extends CharacterEscapes {

	private static final long serialVersionUID = 1L;

	private final int[] asciiEscapes;

	public HTMLCharacterEscapes() {
		asciiEscapes = CharacterEscapes.standardAsciiEscapesForJSON();
		asciiEscapes['<'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['>'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['\"'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['('] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes[')'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['#'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['\''] = CharacterEscapes.ESCAPE_CUSTOM;
	}

	/* (non-Javadoc)
	 * @see com.fasterxml.jackson.core.io.CharacterEscapes#getEscapeCodesForAscii()
	 */
	@Override
	public int[] getEscapeCodesForAscii() {
		int[] safeAsciiEscapes = null;
		if ( this.asciiEscapes != null ) {
			safeAsciiEscapes = this.asciiEscapes;
		}
		return safeAsciiEscapes;
	}

	/* (non-Javadoc)
	 * @see com.fasterxml.jackson.core.io.CharacterEscapes#getEscapeSequence(int)
	 */
	@Override
	public SerializableString getEscapeSequence(int ch) {
		char charAt = (char) ch;
		if ( Character.isHighSurrogate(charAt) || Character.isLowSurrogate(charAt) ) {
			// 이모지 대응
			return new SerializedString(String.format("\\u%04x", ch));
		} else {
			return new SerializedString(StringEscapeUtils.escapeHtml4(Character.toString((char) ch)));
		}
	}

}
