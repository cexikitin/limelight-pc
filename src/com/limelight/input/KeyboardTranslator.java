package com.limelight.input;

import com.limelight.nvstream.NvConnection;
import com.limelight.nvstream.input.KeycodeTranslator;

import java.awt.event.KeyEvent;
import java.util.HashMap;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Class to translate a java key code into the codes GFE is expecting
 * @author Diego Waxemberg
 */
public class KeyboardTranslator extends KeycodeTranslator {

	
	public HashMap<Integer,Integer> KeyCodeMap;
	
    /**
     * GFE's prefix for every key code
     */
    public static final short KEY_PREFIX = (short) 0x80;

    /**
     * Constructs a new translator for the specified connection
     * @param conn the connection to which the translated codes are sent
     */
    public KeyboardTranslator(NvConnection conn) 
    {
    	super(conn);
    	KeyCodeMap = new HashMap<Integer,Integer>();
    	KeyCodeMap.put(GLFW_KEY_SPACE,KeyEvent.VK_SPACE);
    	KeyCodeMap.put(GLFW_KEY_APOSTROPHE,0xde);
    	KeyCodeMap.put(GLFW_KEY_COMMA,0xbc);
    	KeyCodeMap.put(GLFW_KEY_MINUS,0xbd);
    	KeyCodeMap.put(GLFW_KEY_PERIOD,0xbe);
    	KeyCodeMap.put(GLFW_KEY_SLASH,0xbf);
    	KeyCodeMap.put(GLFW_KEY_0,KeyEvent.VK_0);
    	KeyCodeMap.put(GLFW_KEY_1,KeyEvent.VK_1);
    	KeyCodeMap.put(GLFW_KEY_2,KeyEvent.VK_2);
    	KeyCodeMap.put(GLFW_KEY_3,KeyEvent.VK_3);
    	KeyCodeMap.put(GLFW_KEY_4,KeyEvent.VK_4);
    	KeyCodeMap.put(GLFW_KEY_5,KeyEvent.VK_5);
    	KeyCodeMap.put(GLFW_KEY_6,KeyEvent.VK_6);
    	KeyCodeMap.put(GLFW_KEY_7,KeyEvent.VK_7);
    	KeyCodeMap.put(GLFW_KEY_8,KeyEvent.VK_8);
    	KeyCodeMap.put(GLFW_KEY_9,KeyEvent.VK_9);
    	KeyCodeMap.put(GLFW_KEY_SEMICOLON,0xba);
    	KeyCodeMap.put(GLFW_KEY_EQUAL,0xbb);
    	KeyCodeMap.put(GLFW_KEY_A,KeyEvent.VK_A);
    	KeyCodeMap.put(GLFW_KEY_B,KeyEvent.VK_B);
    	KeyCodeMap.put(GLFW_KEY_C,KeyEvent.VK_C);
    	KeyCodeMap.put(GLFW_KEY_D,KeyEvent.VK_D);
    	KeyCodeMap.put(GLFW_KEY_E,KeyEvent.VK_E);
    	KeyCodeMap.put(GLFW_KEY_F,KeyEvent.VK_F);
    	KeyCodeMap.put(GLFW_KEY_G,KeyEvent.VK_G);
    	KeyCodeMap.put(GLFW_KEY_H,KeyEvent.VK_H);
    	KeyCodeMap.put(GLFW_KEY_I,KeyEvent.VK_I);
    	KeyCodeMap.put(GLFW_KEY_J,KeyEvent.VK_J);
    	KeyCodeMap.put(GLFW_KEY_K,KeyEvent.VK_K);
    	KeyCodeMap.put(GLFW_KEY_L,KeyEvent.VK_L);
    	KeyCodeMap.put(GLFW_KEY_M,KeyEvent.VK_M);
    	KeyCodeMap.put(GLFW_KEY_N,KeyEvent.VK_N);
    	KeyCodeMap.put(GLFW_KEY_O,KeyEvent.VK_O);
    	KeyCodeMap.put(GLFW_KEY_P,KeyEvent.VK_P);
    	KeyCodeMap.put(GLFW_KEY_Q,KeyEvent.VK_Q);
    	KeyCodeMap.put(GLFW_KEY_R,KeyEvent.VK_R);
    	KeyCodeMap.put(GLFW_KEY_S,KeyEvent.VK_S);
    	KeyCodeMap.put(GLFW_KEY_T,KeyEvent.VK_T);
    	KeyCodeMap.put(GLFW_KEY_U,KeyEvent.VK_U);
    	KeyCodeMap.put(GLFW_KEY_V,KeyEvent.VK_V);
    	KeyCodeMap.put(GLFW_KEY_W,KeyEvent.VK_W);
    	KeyCodeMap.put(GLFW_KEY_X,KeyEvent.VK_X);
    	KeyCodeMap.put(GLFW_KEY_Y,KeyEvent.VK_Y);
    	KeyCodeMap.put(GLFW_KEY_Z,KeyEvent.VK_Z);
    	KeyCodeMap.put(GLFW_KEY_LEFT_BRACKET,0xdb);
    	KeyCodeMap.put(GLFW_KEY_BACKSLASH,0xdc);
    	KeyCodeMap.put(GLFW_KEY_RIGHT_BRACKET,0xdd);
    	KeyCodeMap.put(GLFW_KEY_GRAVE_ACCENT,KeyEvent.VK_BACK_QUOTE);
    	//KeyCodeMap.put(GLFW_KEY_WORLD_1,);
    	//KeyCodeMap.put(GLFW_KEY_WORLD_2,);


    	KeyCodeMap.put(GLFW_KEY_ESCAPE,KeyEvent.VK_ESCAPE);
    	KeyCodeMap.put(GLFW_KEY_ENTER,0x0d);
    	KeyCodeMap.put(GLFW_KEY_TAB,KeyEvent.VK_TAB );
    	KeyCodeMap.put(GLFW_KEY_BACKSPACE,KeyEvent.VK_BACK_SPACE);
    	KeyCodeMap.put(GLFW_KEY_INSERT,KeyEvent.VK_INSERT);
    	KeyCodeMap.put(GLFW_KEY_DELETE,0x2e);

    	KeyCodeMap.put(GLFW_KEY_LEFT,KeyEvent.VK_LEFT);
    	KeyCodeMap.put(GLFW_KEY_RIGHT,KeyEvent.VK_RIGHT); 
    	KeyCodeMap.put(GLFW_KEY_UP,KeyEvent.VK_UP);
    	KeyCodeMap.put(GLFW_KEY_DOWN,KeyEvent.VK_DOWN);





    	KeyCodeMap.put(GLFW_KEY_PAGE_UP,KeyEvent.VK_PAGE_UP);
    	KeyCodeMap.put(GLFW_KEY_PAGE_DOWN,KeyEvent.VK_PAGE_DOWN);
    	KeyCodeMap.put(GLFW_KEY_HOME,KeyEvent.VK_HOME);
    	KeyCodeMap.put(GLFW_KEY_END,KeyEvent.VK_END);
    	KeyCodeMap.put(GLFW_KEY_CAPS_LOCK,KeyEvent.VK_CAPS_LOCK);
    	KeyCodeMap.put(GLFW_KEY_SCROLL_LOCK,KeyEvent.VK_SCROLL_LOCK);
    	KeyCodeMap.put(GLFW_KEY_NUM_LOCK,KeyEvent.VK_NUM_LOCK);
    	KeyCodeMap.put(GLFW_KEY_PRINT_SCREEN,KeyEvent.VK_PRINTSCREEN);
    	KeyCodeMap.put(GLFW_KEY_PAUSE,KeyEvent.VK_PAUSE );
    	KeyCodeMap.put(GLFW_KEY_F1,KeyEvent.VK_F1);
    	KeyCodeMap.put(GLFW_KEY_F2,KeyEvent.VK_F2);
    	KeyCodeMap.put(GLFW_KEY_F3,KeyEvent.VK_F3);
    	KeyCodeMap.put(GLFW_KEY_F4,KeyEvent.VK_F4);
    	KeyCodeMap.put(GLFW_KEY_F5,KeyEvent.VK_F5);
    	KeyCodeMap.put(GLFW_KEY_F6,KeyEvent.VK_F6);
    	KeyCodeMap.put(GLFW_KEY_F7,KeyEvent.VK_F7);
    	KeyCodeMap.put(GLFW_KEY_F8,KeyEvent.VK_F8);
    	KeyCodeMap.put(GLFW_KEY_F9,KeyEvent.VK_F9);
    	KeyCodeMap.put(GLFW_KEY_F10,KeyEvent.VK_F10);
    	KeyCodeMap.put(GLFW_KEY_F11,KeyEvent.VK_F11);
    	KeyCodeMap.put(GLFW_KEY_F12,KeyEvent.VK_F12);
    	KeyCodeMap.put(GLFW_KEY_F13,KeyEvent.VK_F13);
    	KeyCodeMap.put(GLFW_KEY_F14,KeyEvent.VK_F14);
    	KeyCodeMap.put(GLFW_KEY_F15,KeyEvent.VK_F15);
    	KeyCodeMap.put(GLFW_KEY_F16,KeyEvent.VK_F16);
    	KeyCodeMap.put(GLFW_KEY_F17,KeyEvent.VK_F17);
    	KeyCodeMap.put(GLFW_KEY_F18,KeyEvent.VK_F18);
    	KeyCodeMap.put(GLFW_KEY_F19,KeyEvent.VK_F19);
    	KeyCodeMap.put(GLFW_KEY_F20,KeyEvent.VK_F20);
    	KeyCodeMap.put(GLFW_KEY_F21,KeyEvent.VK_F21);
    	KeyCodeMap.put(GLFW_KEY_F22,KeyEvent.VK_F22);
    	KeyCodeMap.put(GLFW_KEY_F23,KeyEvent.VK_F23);
    	KeyCodeMap.put(GLFW_KEY_F24,KeyEvent.VK_F24);
    	KeyCodeMap.put(GLFW_KEY_KP_0,KeyEvent.VK_NUMPAD0);
    	KeyCodeMap.put(GLFW_KEY_KP_1,KeyEvent.VK_NUMPAD1);
    	KeyCodeMap.put(GLFW_KEY_KP_2,KeyEvent.VK_NUMPAD2);
    	KeyCodeMap.put(GLFW_KEY_KP_3,KeyEvent.VK_NUMPAD3);
    	KeyCodeMap.put(GLFW_KEY_KP_4,KeyEvent.VK_NUMPAD4);
    	KeyCodeMap.put(GLFW_KEY_KP_5,KeyEvent.VK_NUMPAD5);
    	KeyCodeMap.put(GLFW_KEY_KP_6,KeyEvent.VK_NUMPAD6);
    	KeyCodeMap.put(GLFW_KEY_KP_7,KeyEvent.VK_NUMPAD7);
    	KeyCodeMap.put(GLFW_KEY_KP_8,KeyEvent.VK_NUMPAD8);
    	KeyCodeMap.put(GLFW_KEY_KP_9,KeyEvent.VK_NUMPAD9);
    	KeyCodeMap.put(GLFW_KEY_KP_DECIMAL,KeyEvent.VK_DECIMAL);
    	KeyCodeMap.put(GLFW_KEY_KP_DIVIDE,KeyEvent.VK_DIVIDE);
    	KeyCodeMap.put(GLFW_KEY_KP_MULTIPLY,KeyEvent.VK_MULTIPLY);
    	KeyCodeMap.put(GLFW_KEY_KP_SUBTRACT,KeyEvent.VK_SUBTRACT);
    	KeyCodeMap.put(GLFW_KEY_KP_ADD,KeyEvent.VK_ADD);

    	//Not sure what java does by default in these cases...
    	//KeyCodeMap.put(GLFW_KEY_KP_ENTER,);
    	//KeyCodeMap.put(GLFW_KEY_KP_EQUAL,);
    	//KeyCodeMap.put(GLFW_KEY_LEFT_SHIFT,);
    	//KeyCodeMap.put(GLFW_KEY_LEFT_CONTROL,);
    	//KeyCodeMap.put(GLFW_KEY_LEFT_ALT,);
    	//KeyCodeMap.put(GLFW_KEY_LEFT_SUPER,);
    	//KeyCodeMap.put(GLFW_KEY_RIGHT_SHIFT,);
    	//KeyCodeMap.put(GLFW_KEY_RIGHT_CONTROL,);
    	//KeyCodeMap.put(GLFW_KEY_RIGHT_ALT,);
    	//KeyCodeMap.put(GLFW_KEY_RIGHT_SUPER,);
    	KeyCodeMap.put(GLFW_KEY_MENU,KeyEvent.VK_CONTEXT_MENU);
        
    }

    /**
     * Translates the given keycode and returns the GFE keycode
     * @param keycode the code to be translated
     * @return a GFE keycode for the given keycode
     */
    @Override
    public short translate(int keycode) 
    {

    	int key = KeyCodeMap.getOrDefault(keycode, -1);
    	if (key == -1)
    	{
    		return -1;
    	}
    	

        return (short) ((KEY_PREFIX << 8) | key);
    }

}