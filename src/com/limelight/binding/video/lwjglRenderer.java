package com.limelight.binding.video;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL2;






import org.lwjgl.BufferUtils;
import org.lwjgl.Sys;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

import com.limelight.LimeLog;
import com.limelight.input.KeyboardTranslator;
import com.limelight.nvstream.NvConnection;
import com.limelight.nvstream.av.ByteBufferDescriptor;
import com.limelight.nvstream.av.DecodeUnit;
import com.limelight.nvstream.av.video.VideoDecoderRenderer;
import com.limelight.nvstream.av.video.VideoDepacketizer;
import com.limelight.nvstream.av.video.cpu.AvcDecoder;
import com.limelight.nvstream.input.KeyboardPacket;
import com.limelight.nvstream.input.MouseButtonPacket;

public class lwjglRenderer extends VideoDecoderRenderer {
	private Thread decoderThread;
	private Thread renderThread;
	protected int width, height, targetFps;
	protected boolean dying;

	private static final int DECODER_BUFFER_SIZE = 92 * 1024;
	private ByteBuffer decoderBuffer;

	private IntBuffer bufferRGB;

	private NvConnection conn;
	private int totalFrames;
	private long totalDecoderTimeMs;

	private float viewportX, viewportY;
	
	private static KeyboardTranslator translator;
	
    private GLFWErrorCallback errorCallback;
    private GLFWKeyCallback   keyCallback;
    private GLFWMouseButtonCallback   mouseButtonCallback;
    private GLFWCursorPosCallback cursorPosCallback;
    private GLFWScrollCallback scrollCallback;
    // The window handle
    private long window;
    private boolean mouseCaptured = true;

	/**
	 * Sets up the decoder and renderer to render video at the specified
	 * dimensions
	 * 
	 * @param width
	 *            the width of the video to render
	 * @param height
	 *            the height of the video to render
	 * @param connection
	 *            nvconnection object
	 * @param drFlags
	 *            flags for the decoder and renderer
	 */
	public boolean setup(int width, int height, int redrawRate, Object connection, int drFlags) {
		this.width = width;
		this.height = height;
		this.targetFps = redrawRate;
		conn = (NvConnection) connection;
		decoderBuffer = ByteBuffer.allocate(DECODER_BUFFER_SIZE
				+ AvcDecoder.getInputPaddingSize());
		LimeLog.info("Using software decoding");
		
		translator = new KeyboardTranslator(conn);
		// Use 2 decoding threads
		int avcFlags = AvcDecoder.FAST_BILINEAR_FILTERING
				| AvcDecoder.NATIVE_COLOR_0RGB;
		int threadCount = 2;

		int err = AvcDecoder.init(width, height, avcFlags, threadCount);
		if (err != 0) {
			LimeLog.severe("AVC decoder initialization failure: " + err);
			return false;
		}
		
		ByteBuffer wtf = ByteBuffer.allocateDirect((width * height)* 4);
		bufferRGB = wtf.asIntBuffer();
		bufferRGB.hasArray();

		viewportX = width;
		viewportY = height;

		return true;
		// return setupInternal(renderTarget, drFlags);
	}

	/**
	 * Starts the decoding and rendering of the video stream on a new thread
	 */
	public boolean start(final VideoDepacketizer depacketizer) {
		decoderThread = new Thread() {
			@Override
			public void run() {

				DecodeUnit du;
				while (!dying) {
					try {
						du = depacketizer.takeNextDecodeUnit();
					} catch (InterruptedException e1) {
						return;
					}

					if (du != null) {
						submitDecodeUnit(du);
						depacketizer.freeDecodeUnit(du);
					}

				}
			}
		};
		decoderThread.setPriority(Thread.MAX_PRIORITY - 1);
		decoderThread.setName("Video - Decoder (CPU)");
		decoderThread.start();

		renderThread = new Thread() {
			@Override
			public void run()
			{
				
				try {
				init();
	            loop();
	            
	            // Release window and window callbacks
	            glfwDestroyWindow(window);
	            keyCallback.release();
	            mouseButtonCallback.release();
	            cursorPosCallback.release();
	            scrollCallback.release();
	            
	            
				}
	            finally {
	                // Terminate GLFW and release the GLFWerrorfun
	                glfwTerminate();
	                errorCallback.release();
	            }

			}
		};
		
		renderThread.setPriority(Thread.MAX_PRIORITY - 2);
		renderThread.setName("Video - Renderer");
		renderThread.start();

		return true;
	}
	
    private void loop() 
    {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the ContextCapabilities instance and makes the OpenGL
        // bindings available for use.
        GLContext.createFromCurrent();
 
        // Set the clear color
        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( glfwWindowShouldClose(window) == GL_FALSE ) 
        {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
 
            AvcDecoder.getRgbFrameIntDirect(bufferRGB, width * height);
            
            glRasterPos2i(-1, 1);
            glPixelZoom(viewportX / width, -(viewportY / height));
            glDrawPixels(width, height, GL2.GL_BGRA, GL2.GL_UNSIGNED_INT_8_8_8_8_REV, bufferRGB);
            
            glfwSwapBuffers(window); // swap the color buffers
 
            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }
    }

    private void init() 
    {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        glfwSetErrorCallback(errorCallback = errorCallbackPrint(System.err));
 
        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( glfwInit() != GL11.GL_TRUE )
            throw new IllegalStateException("Unable to initialize GLFW");
 
        // Configure our window
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GL_TRUE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GL_FALSE); // the window will be resizable
 

 
        // Create the window
        window = glfwCreateWindow(width, height, "Hello World!", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");
        
        
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        
        glfwSetScrollCallback(window, scrollCallback = new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double xoffset, double yoffset) 
            {
            	if (mouseCaptured)
            	{
            		conn.sendMouseScroll((byte) yoffset);
            	}
            }
        });
        
        
        
        
        glfwSetMouseButtonCallback(window, mouseButtonCallback = new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) 
            {
            	if (!mouseCaptured && action == GLFW_PRESS)
            	{
            		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
            		
        			DoubleBuffer x = BufferUtils.createDoubleBuffer(1);
        			DoubleBuffer y = BufferUtils.createDoubleBuffer(1);
        			glfwGetCursorPos(window,x,y);
        			x.rewind();
        			y.rewind();
        			last_x = x.get();
        			last_y = y.get();
            		mouseCaptured = true;
            		return;
            	}
            		byte mouseButton = getButtonFromEvent(button);
            		if (mouseButton > 0)
            		{
            			if (action == GLFW_PRESS)
            			{
            				conn.sendMouseButtonDown(mouseButton);
            			}
            			else if (action == GLFW_RELEASE)
            			{
            				conn.sendMouseButtonUp(mouseButton);
            			}
            		}
            	
            }
        });
        
        
        
        glfwSetCursorPosCallback(window, cursorPosCallback = new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xpos, double ypos) 
            {
            	if (mouseCaptured)
            	{
        			conn.sendMouseMove((short)(xpos - last_x), (short)(ypos - last_y));
        			last_x = xpos;
        			last_y = ypos;
            	}
            }
        });
        
        
        
        
        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) 
            {
                //if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                //    glfwSetWindowShouldClose(window, GL_TRUE); // We will detect this in our rendering loop

				if (action == GLFW_PRESS || action == GLFW_REPEAT)
				{
					keyPressed(key,mods);
				}
				else if (action == GLFW_RELEASE)
				{
					keyReleased(key,mods);
				}   
                
            }
        });
 
        // Get the resolution of the primary monitor
        ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        // Center our window
        glfwSetWindowPos(
            window,
            (GLFWvidmode.width(vidmode) - width) / 2,
            (GLFWvidmode.height(vidmode) - height) / 2
        );
 
        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);
 
        // Make the window visible
        glfwShowWindow(window);
    }
    
    public void keyReleased(int key,int modifiers) 
    {


        short keyMap = translator.translate(key);


        
        byte modifier = 0x0;


        if ((modifiers & GLFW_MOD_SHIFT) != 0) {
            modifier |= KeyboardPacket.MODIFIER_SHIFT;
        }
        if ((modifiers & GLFW_MOD_CONTROL) != 0) {
            modifier |= KeyboardPacket.MODIFIER_CTRL;
        }
        if ((modifiers & GLFW_MOD_ALT) != 0) {
            modifier |= KeyboardPacket.MODIFIER_ALT;
        }

        
        if (keyMap == -1 || !mouseCaptured)
        	return;
        
        translator.sendKeyUp(keyMap, modifier);
    }
    
    public void keyPressed(int key,int modifiers) 
    {


        short keyMap = translator.translate(key);

System.out.println(String.format("%d %d",key,modifiers));
        
        byte modifier = 0x0;
        if ((modifiers & GLFW_MOD_SHIFT) != 0) 
        {
            modifier |= KeyboardPacket.MODIFIER_SHIFT;
        }
        if ((modifiers & GLFW_MOD_CONTROL) != 0) 
        {
            modifier |= KeyboardPacket.MODIFIER_CTRL;
        }
        if ((modifiers & GLFW_MOD_ALT) != 0) 
        {
            modifier |= KeyboardPacket.MODIFIER_ALT;
        }

        if ((modifiers & GLFW_MOD_SHIFT) != 0 && (modifiers & GLFW_MOD_ALT) != 0 && (modifiers & GLFW_MOD_CONTROL) != 0 && key == GLFW_KEY_Q) 
        {
            LimeLog.info("quitting");
            glfwSetWindowShouldClose(window, GL_TRUE);
            return;
        } 
        else if (
                (modifiers & GLFW_MOD_SHIFT) != 0 &&
                (modifiers & GLFW_MOD_ALT) != 0 &&
                (modifiers & GLFW_MOD_CONTROL) != 0) {
            if (mouseCaptured) 
            {
            	glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
            } 
            else 
            {
            	glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
            }
            mouseCaptured = !mouseCaptured;
            return;
        }

        if (keyMap == -1 || !mouseCaptured)
        	return;

        translator.sendKeyDown(keyMap, modifier);
    }
    
    
	/**
	 * Stops the decoding and rendering of the video stream.
	 */
	public void stop() {
		dying = true;
		decoderThread.interrupt();

		try {
			decoderThread.join();
		} catch (InterruptedException e) {
		}
	}

	/**
	 * Releases resources held by the decoder.
	 */
	public void release() {
		AvcDecoder.destroy();
	}

	/**
	 * Give a unit to be decoded to the decoder.
	 * 
	 * @param decodeUnit
	 *            the unit to be decoded
	 * @return true if the unit was decoded successfully, false otherwise
	 */
	public boolean submitDecodeUnit(DecodeUnit decodeUnit) {
		byte[] data;

		// Use the reserved decoder buffer if this decode unit will fit
		if (decodeUnit.getDataLength() <= DECODER_BUFFER_SIZE) {
			decoderBuffer.clear();

			for (ByteBufferDescriptor bbd = decodeUnit.getBufferHead(); bbd != null; bbd = bbd.nextDescriptor) {
				decoderBuffer.put(bbd.data, bbd.offset, bbd.length);
			}

			data = decoderBuffer.array();
		} else {
			data = new byte[decodeUnit.getDataLength()
					+ AvcDecoder.getInputPaddingSize()];

			int offset = 0;
			for (ByteBufferDescriptor bbd = decodeUnit.getBufferHead(); bbd != null; bbd = bbd.nextDescriptor) {
				System.arraycopy(bbd.data, bbd.offset, data, offset, bbd.length);
				offset += bbd.length;
			}
		}

		boolean success = (AvcDecoder.decode(data, 0,
				decodeUnit.getDataLength()) == 0);
		if (success) {
			long timeAfterDecode = System.currentTimeMillis();

			// Add delta time to the totals (excluding probable outliers)
			long delta = timeAfterDecode - decodeUnit.getReceiveTimestamp();
			if (delta >= 0 && delta < 300) {
				totalDecoderTimeMs += delta;
				totalFrames++;
			}
		}

		return true;
	}

	private double last_x, last_y;

	private static byte getButtonFromEvent(int e) {
		if (e == GLFW_MOUSE_BUTTON_LEFT) 
		{
			return MouseButtonPacket.BUTTON_LEFT;
		} else if (e == GLFW_MOUSE_BUTTON_MIDDLE) 
		{
			return MouseButtonPacket.BUTTON_MIDDLE;
		} else if (e == GLFW_MOUSE_BUTTON_RIGHT ) 
		{
			return MouseButtonPacket.BUTTON_RIGHT;
		}

		return 0;
	}

	public int getCapabilities() {
		return 0;
	}

	public int getAverageDecoderLatency() {
		if (totalFrames == 0) {
			return 0;
		}
		return (int) (totalDecoderTimeMs / totalFrames);
	}
}
