package com.xf.js;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;

public class Asm{
    ByteOrder byteOrder;
    ByteArrayOutputStream baos;
    java.io.DataOutputStream dos;

    static int STDOUT						= 1;

    static int SYSCALL_EXIT					= 0x2000001;
    static int SYSCALL_WRITE				= 0x2000004;
    
    static byte[] syscall		 			= {0x0f, 0x05};
    static byte[] mov_eax_int32 			= {(byte)0xb8};//, 0x01, 0x00, 0x00, 0x02};    
    static byte[] mov_ebx_int32 			= {(byte)0xbb};//, 0x00, 0x00, 0x00, 0x00};
    static byte[] mov_rax_int32 			= {0x48, (byte)0xc7, (byte)0xc0}; //, 0x01, 0x00, 0x00, 0x02
    static byte[] mov_rax_int64 			= {0x48, (byte)0xB8}; //, (byte)0x89, 0x67, 0x45, 0x23, 0x01, 0x00, 0x00, 0x00 }
    static byte[] mov_rdx_int32				= {(byte)0xba}; //, 0x0e, 0x00, 0x00, 0x00
    static byte[] mov_rdi_int32				= {(byte)0xbf}; //, 0x01, 0x00, 0x00, 0x00};
    static byte[] lea_rsi_rip_offset32 		= {0x48, (byte)0x8d, 0x35};
    static byte[] mov_rsi_int64				= {0x48, (byte)0xBE}; // 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
    static byte[] xor_rdi_rdi 				= {0x48, 0x31, (byte)0xff};
    static byte[] nop					 	= {(byte)0x90};
    
    public Asm() {
    	this(ByteOrder.LITTLE_ENDIAN);
    }
	public Asm(ByteOrder byteorder) {
        baos = new ByteArrayOutputStream();
        dos = new java.io.DataOutputStream(baos);
    	byteOrder = byteorder;
    }
    public void put(byte[] code) {
    	try {
			dos.write(code);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    public void put(byte code) {
    	try {
			dos.write(code);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    public void put(short i16) {
    	try {
			dos.writeShort(i16(i16));
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    public void put(int i32) {
    	try {
			dos.writeInt(i32(i32));
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    public void put(long i64) {
    	try {
			dos.writeLong(i64(i64));
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    public short i16(int v) {
    	return (short)i32(v);
    }
    public int i32(int v) {
    	if(byteOrder == ByteOrder.LITTLE_ENDIAN) {
    		v = Integer.reverseBytes(v);
    	}
    	return v;
    }
    public long i64(long v) {
    	if(byteOrder == ByteOrder.LITTLE_ENDIAN) {
    		v = Long.reverseBytes(v);
    	}
    	return v;
    }
    public byte[] toBytes() {
    	byte[] code = baos.toByteArray();
    	return code;
    }

    public void syscall() {
    	put(syscall);
    }
    public void syscall(int func) {
    	mov_rax(func);
    	syscall();
    }
    public void syscall(int func, long p1) {
        mov_rdi((int)p1);
    	mov_rax(func);
    	syscall();
    }
    //Macos: rax = func, rdi = pi, rsi = p2, rdx = p3, rcx = p4, r8 = p5, r9 = p6
    public void syscall(int func, long p1, long p2, long p3) {
        mov_rdi((int)p1);
        mov_rsi(p2);
        mov_rdx((int)p3);
    	mov_rax(func);
    	syscall();
    }
    public void mov_rax(int i32) {
    	put(mov_rax_int32);
    	put(i32);
    }
    public void mov_rax(long i64) {
    	put(mov_rax_int64);
    	put(i64);
    }
    public void mov_rdx(int i32) {
    	put(mov_rdx_int32);
    	put(i32);
    }
    public void mov_rdi(int i32) {
    	put(mov_rdi_int32);
    	put(i32);
    }
    public void xor_rdi_rdi() {
    	put(xor_rdi_rdi);
    }
    public void mov_ebx(int i32) {
    	put(mov_ebx_int32);
    	put(i32);
    }
    public void mov_eax(int i32) {
    	put(mov_eax_int32);
    	put(i32);
    }
    //lea rsi,[rip+imm32]
    public void lea_rsi_rip_offset32(int i32) {
    	put(lea_rsi_rip_offset32);
    	put(i32);
    }
    public void mov_rsi(long i64) {
    	put(mov_rsi_int64);
    	put(i64);
    }
    public void nop() {
    	put(nop);
    }
    public void put(String s) {
    	put(s.getBytes());
	}
}
