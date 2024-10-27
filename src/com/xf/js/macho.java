package com.xf.js;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class macho {
	static int MH_MAGIC_64			= 0xfeedfacf; /* the 64-bit mach magic number */
	static int MH_EXECUTE			= 0x2;		/* demand paged executable file */

    static int MH_NOUNDEFS			= 0x00000001;		// Flags (e.g., MH_NOUNDEFS = 0x00000001)
    static int CPU_TYPE_X86_64		= 0x01000007;
    static int CPU_SUBTYPE_X86_64_ALL	= 0x00000003;
//
	static int VM_PROT_READ			= 1;
	static int VM_PROT_WRITE		= 2;
	static int VM_PROT_EXECUTE		= 4;
	
	static int LC_REQ_DYLD 			= 0x80000000;
	static int LC_SYMTAB			= 0x02;
	static int LC_DYSYMTAB			= 0x0b;
	static int LC_LOAD_DYLIB		= 0x0c;
	static int LC_LOAD_DYLINKER		= 0x0e;
	static int LC_SEGMENT_64 		= 0x19;
	static int LC_DYLD_INFO			= 0x22;
	static int LC_DYLD_INFO_ONLY	= LC_DYLD_INFO | LC_REQ_DYLD;
	static int LC_MAIN 				= 0x80000028;
	//
    String outputFile 				= null;

    public static void main(String[] args) {
        String outputFile = "out/hello";
    	macho m = new macho(outputFile);
    	m.writeFile();
    }

    public macho(String outputFile){
    	this.outputFile = outputFile;
    }
    
    public void writeFile(){        
        macho_header64 header = new macho_header64();
        
        segment_command_64 pagezero = new segment_command_64("__PAGEZERO", LC_SEGMENT_64);
        pagezero.vmsize =  0x100000000l;

        segment_command_64 text = new segment_command_64("__TEXT", LC_SEGMENT_64);
        section_64 sec = new section_64("__text","__TEXT");
        section_64 sec2 = new section_64("__unwind_info","__TEXT");
        text.appendSection(sec);
//        text.appendSection(sec2);
        text.vmaddr = 0x100000000l;
        text.vmsize = 0x1000;
        text.filesize = 4096;
        text.maxprot = 7;
        text.initprot = 5;

        segment_command_64 data = new segment_command_64("__DATA", LC_SEGMENT_64);
        section_64 data_sec = new section_64("__data","__DATA");
        data.appendSection(data_sec);

        segment_command_64 linkedit = new segment_command_64("__LINKEDIT", LC_SEGMENT_64);

        symtab_command symtab = new symtab_command();
        dysymtab_command dysymtab = new dysymtab_command();
        entry_point_command main_entry = new entry_point_command(0,0);
        dyld_info_command ld_info = new dyld_info_command();
        dylinker_command linker = new dylinker_command("/usr/lib/dyld");
        dylib_command lib = new dylib_command("/usr/lib/libSystem.B.dylib");

        appendCommand(pagezero);
        appendCommand(text);
        appendCommand(data);
        appendCommand(linkedit);	// not necessary
        appendCommand(symtab);
        appendCommand(dysymtab);
        appendCommand(main_entry);
        appendCommand(ld_info);	// not necessary
        appendCommand(linker);
        appendCommand(lib);		// not necessary

        byte[] code = makeCode();
        byte[] dataSeg = new byte[4096];
        System.arraycopy("Hello, World!\n".getBytes(), 0, dataSeg, 0, 14);

        sec.offset = sizeofcmds + 0x20;	// 0x20 is the length of macho header
        sec.size = code.length;
        sec.addr = 0x0100000000l + sec.offset;
        sec.flags = 0x80000400;
        sec2.offset = (int) (sec.offset + sec.size);
        sec2.size = 10;
        sec2.addr = sec.addr + sec.size;
        sec2.align = 2;
        
        main_entry.entryoff = sec.offset;
        		
        data.vmaddr = 0x0000000100001000l;
        data.vmsize = dataSeg.length;
        data.maxprot = 7;
        data.initprot = 3;
        data.fileoff = 4096;	//sec.offset + code.length;
        data.filesize = dataSeg.length;
        
        data_sec.addr = 0x0000000100001000l;
        data_sec.size =  0x100;
        data_sec.offset = 4096; //sec.offset + code.length;
        
        header.ncmds = cmds.size();
        header.sizeofcmds = sizeofcmds;
//        text.filesize = 1024;//+4096;
        
        // write to file
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            byte[] machoHeader = header.toArray(); 
            fos.write(machoHeader);
            int pos = machoHeader.length;
            for(int i=0;i<cmds.size();i++) {
            	base cmd = cmds.get(i);
                byte[] bytes = cmd.toArray();
                pos += bytes.length;
                fos.write(bytes); 
                pln("Write comand " + i +", size=" + bytes.length +" bytes, " + cmd);
            }

            fos.write(code);
            pos += code.length;
            byte[] blank = new byte[4096 - pos % 4096];
            fos.write(blank);
            fos.write(dataSeg);            // 写入字符串 "Hello, World!\n" 到数据段

            System.out.println("Mach-O file with 'Hello, World!' created at " + outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    static void pln(String log) {
    	System.out.println(log);
    }

    // 写入代码段，执行系统调用，输出 "Hello, World!"
    private byte[] makeCode() {
        String hello = "Hello, Code!\n";
        Asm asm = new Asm();

        asm.syscall(Asm.SYSCALL_WRITE, Asm.STDOUT, 0x0100001000l, 14);
	    
        asm.mov_rdi(Asm.STDOUT);			// mov rdi, 1 (stdout)
        asm.mov_rdx(hello.length());		// mov rdx, 14 (message length)
        asm.lea_rsi_rip_offset32(24);		// lea rsi, [rip+msg] (load address of "Hello, World!" message)
        									// 24= 0x37e - 0x35f - 7
        asm.syscall(Asm.SYSCALL_WRITE);

        asm.syscall(Asm.SYSCALL_EXIT, 0);	// echo $?
        
        asm.nop();
        asm.put(hello);
        byte[] code = asm.toBytes();
        return code;
	}
    private void putInt(ByteBuffer bb, byte[] code, int v) {
        bb.put(code);
        bb.putInt(v);    	
    }
	List<base> cmds = new ArrayList<base>();
	int sizeofcmds = 0;
	public void appendCommand(base cmd) {
    	cmds.add(cmd);
    	sizeofcmds += cmd.toArray().length; // cmd.cmdsize();	// add size of section
    }


	
	static class base{
        byte[] toArray() {
        	byte[]bytes = null;
        	try {
	        	Field[] fields = super.getClass().getDeclaredFields();
	//        	fields = this.getClass().getSuperclass().getDeclaredFields();
                ByteBuffer buf = ByteBuffer.allocate(8192);
                
                buf.order(ByteOrder.LITTLE_ENDIAN);
	        	for(Field f:fields) {
	        		Class<?> type = f.getType();
//	        		pln(f.toString());
	        		if(type == int.class) {
	                    buf.putInt(f.getInt(this));         			
	        		}
	        		else if(type == long.class) {
	                    buf.putLong(f.getLong(this));         			
	        		}
	        		else if(type == String.class) {
	        			byte[] name = new byte[16];
	        			String s = (String)f.get(this);
	        			int len = (s.length() > name.length)?name.length:s.length();
	        			System.arraycopy(s.getBytes(), 0, name, 0, len);
	                    buf.put(name); 
	        		}
	        		else if(type == byte[].class) {
	                    buf.put((byte[])f.get(this));
	        		}
	        	}
	        	bytes = new byte[buf.position()];
	        	System.arraycopy(buf.array(), 0, bytes, 0, buf.position());	        	
        	}
        	catch(Exception e) {
        		System.out.println(e.toString());
        	}
        	return bytes;
        }
        int align8(int n) {
        	if(n % 8 > 0)
        		n += 8 - n % 8;
			return n;
        }
        int align16(int n) {
        	if(n % 16 > 0)
        		n += 16 - n % 16;
			return n;
        }
	}
	
	static class macho_header64 extends base {
		int magic		= MH_MAGIC_64;       		// Magic number for 64-bit Mach-O (0xfeedfacf)
    	int cputype		= CPU_TYPE_X86_64;	    	// CPU type (CPU_TYPE_X86_64 = 0x01000007)
        int cpusubtype	= CPU_SUBTYPE_X86_64_ALL;	// CPU subtype (CPU_SUBTYPE_X86_64_ALL = 0x00000003)
        int filetype	= MH_EXECUTE;				// File type (MH_EXECUTE = 0x00000002, executable file)
        int ncmds		= 0;						// Number of load commands        // LC_SEGMENT_64 + LC_MAIN
        int sizeofcmds	= 0; //56+16 + 24; 				// 56 bytes for LC_SEGMENT_64, 24 bytes for LC_MAIN
        int flags		= MH_NOUNDEFS;
    	int reserved	= 0;
    	
    	macho_header64(){    		
    	}
    }
	
	static class segment_command_64 extends base{ /* for 64-bit architectures */
		int cmd;        /* LC_SEGMENT_64 */
		int cmdsize;    /* includes sizeof section_64 structs */
		String segname;	/* segment name */ //16 bytes
        long vmaddr; 	/* memory address of this segment */
        long vmsize; 	/* memory size of this segment */
        long fileoff;	/* file offset of this segment */
        long filesize;	/* amount to map from the file */
        int maxprot;	/* maximum VM protection */// maxprot (read, write, execute) // VM_PROT_READ | VM_PROT_WRITE | VM_PROT_EXECUTE
        int initprot;	/* initial VM protection */// initprot (read, execute) // VM_PROT_READ | VM_PROT_EXECUTE
        int nsects;		/* number of sections in segment */// Number of sections
        int flags;		/* flags */
    	List<section_64> sections = new ArrayList<section_64>();
        
        public segment_command_64(String _segname, int _cmd) {
            segname = _segname;
            cmd = _cmd;
            cmdsize = 72; // 0 section 
		}
    	
        public void appendSection(section_64 sec) {
        	sections.add(sec);
        	nsects ++;
        	cmdsize += 80;	// add size of section
        }
        public byte[] toArray() {
        	byte[] bytes = super.toArray();
            ByteBuffer buf = ByteBuffer.allocate(8192);
            buf.put(bytes);
            for(section_64 s:sections) {
            	buf.put(s.toArray());
            }
        	bytes = new byte[buf.position()];
        	System.arraycopy(buf.array(), 0, bytes, 0, buf.position());

        	return bytes;
        }
	}

	static class symtab_command extends base{
		int cmd; 		/* LC_SYMTAB */
		int cmdsize;	/* sizeof(class symtab_command) */
        int symoff;		/* symbol table offset */
        int nsyms;		/* number of symbol table entries */
        int stroff;		/* string table offset */
        int strsize;	/* string table size in bytes */
        
        public symtab_command() {
            this.cmd = LC_SYMTAB;
            cmdsize = 24;
		}
	}
	
	static class section_64 extends base{ /* for 64-bit architectures */
		String sectname;	/* name of this section, byte[16] */
		String segname;	/* segment this section goes in, byte[16] */
		long addr;		/* memory address of this section */
		long size;		/* size in bytes of this section */
		int	offset;		/* file offset of this section */
		int	align;		/* section alignment (power of 2) */
		int	reloff;		/* file offset of relocation entries */
		int	nreloc;		/* number of relocation entries */
		int	flags;		/* flags (section type and attributes)*/
		int	reserved1;	/* reserved (for offset or index) */
		int	reserved2;	/* reserved (for count or sizeof) */
		int	reserved3;	/* reserved */
		
		section_64(String _sectname, String _segname) {
			sectname = _sectname;
			segname = _segname;
		}
	}

	
	static class dysymtab_command extends base{
	    int cmd;		/* LC_DYSYMTAB */
	    int cmdsize;	/* sizeof(class dysymtab_command) */
	    int ilocalsym;	/* index to local symbols */
	    int nlocalsym;	/* number of local symbols */
	    int iextdefsym;	/* index to externally defined symbols */
	    int nextdefsym;	/* number of externally defined symbols */
	    int iundefsym;	/* index to undefined symbols */
	    int nundefsym;	/* number of undefined symbols */
	    int tocoff;		/* file offset to table of contents */
	    int ntoc;		/* number of entries in table of contents */
	    int modtaboff;	/* file offset to module table */
	    int nmodtab;	/* number of module table entries */
	    int extrefsymoff;	/* offset to referenced symbol table */
	    int nextrefsyms;	/* number of referenced symbol table entries */
	    int indirectsymoff; /* file offset to the indirect symbol table */
	    int nindirectsyms;  /* number of indirect symbol table entries */
	    int extreloff;	/* offset to external relocation entries */
	    int nextrel;	/* number of external relocation entries */
	    int locreloff;	/* offset to local relocation entries */
	    int nlocrel;	/* number of local relocation entries */

	    dysymtab_command() {
	    	cmd = LC_DYSYMTAB;
	    	cmdsize = 80;
	    }
	}

	static class dyld_info_command extends base{
		int   cmd;		/* LC_DYLD_INFO or LC_DYLD_INFO_ONLY */
		int   cmdsize;		/* sizeof(class dyld_info_command) */
	    int   rebase_off;	/* file offset to rebase info  */
	    int   rebase_size;	/* size of rebase info   */
	    int   bind_off;	/* file offset to binding info   */
	    int   bind_size;	/* size of binding info  */
	    int   weak_bind_off;	/* file offset to weak binding info   */
	    int   weak_bind_size;  /* size of weak binding info  */	    
	    int   lazy_bind_off;	/* file offset to lazy binding info */
	    int   lazy_bind_size;  /* size of lazy binding infs */
	    int   export_off;	/* file offset to lazy binding info */
	    int   export_size;	/* size of lazy binding infs */
	    
	    dyld_info_command() {
	    	cmd = LC_DYLD_INFO_ONLY;
	    	cmdsize = 48;
	    }
	};

	static class dylinker_command extends base{
		int	cmd;		/* LC_ID_DYLINKER, LC_LOAD_DYLINKER or
						   LC_DYLD_ENVIRONMENT */
		int	cmdsize;	/* includes pathname string, 8 bytes align? */
//		lc_str name;
		int name_offset;		/* dynamic linker's path name */
	    byte[] linker_path;
	    byte[] align_zero;

		dylinker_command(String linker) {
			cmd = LC_LOAD_DYLINKER;
			cmdsize = 12 + linker.length();
			int size = align8(cmdsize);
			align_zero = new byte[size-cmdsize];
			cmdsize = size;
			name_offset = 12;
			linker_path = linker.getBytes();
		}
	};


	static class dylib_command extends base{
		int	cmd;			/* LC_ID_DYLIB, LC_LOAD_{,WEAK_}DYLIB,
						   		LC_REEXPORT_DYLIB */
		int	cmdsize;		/* includes pathname string */
//		dylib dylib;		/* the library identification */
		int name_offset;			/* lc_str library's path name, 8bytes align */
	    int timestamp;				/* library's build time stamp */
	    int current_version;		/* library's current version number */
	    int compatibility_version;	/* library's compatibility vers number*/
	    byte[] lib_path;
	    byte[] align_zero;
	    
		dylib_command(String _lib_path) {
			cmd = LC_LOAD_DYLIB;
			cmdsize = 24 + _lib_path.length();
			int size = align8(cmdsize);
			align_zero = new byte[size-cmdsize];
			cmdsize = size;
			name_offset = 24;
			lib_path = _lib_path.getBytes();
		}
	};
	
	static class entry_point_command extends base{
		int  cmd;			/* LC_MAIN only used in MH_EXECUTE filetypes */
		int  cmdsize;		/* 24 */
	    long  entryoff;		/* file (__TEXT) offset of main() */
	    long  stacksize;	/* if not zero, initial stack size */
	    
	    entry_point_command(long _entryoff, long _stacksize) {
	    	cmd = LC_MAIN;
	    	cmdsize = 24;
	    	entryoff = _entryoff;
			stacksize = _stacksize;
	    }
	};

}
