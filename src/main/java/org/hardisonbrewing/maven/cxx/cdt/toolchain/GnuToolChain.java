/**
 * Copyright (c) 2012 Martin M Reed
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.hardisonbrewing.maven.cxx.cdt.toolchain;

import generated.org.eclipse.cdt.StorageModule.Configuration;

import org.hardisonbrewing.maven.cxx.cdt.CProjectService;

public final class GnuToolChain implements ToolChain {

    public static final String ID = "cdt.managedbuild.config.gnu";
    public static final String ID_OSX = "cdt.managedbuild.config.macosx";

    private final Options options;
    private final Builder builder;

    private final CCompiler cCompiler;
    private final CppCompiler cppCompiler;
    private final Assembler assembler;
    private final CLinker cLinker;
    private final CppLinker cppLinker;
    private final Archiver archiver;

    private final Configuration configuration;
    private final generated.org.eclipse.cdt.ToolChain toolChain;

    public GnuToolChain(Configuration configuration) {

        // these need to be set first because Tools,
        // Options and Builder may try to reference toolChain
        this.configuration = configuration;
        toolChain = CProjectService.getToolChain( configuration );

        options = new Options( this );
        builder = new Builder( this );

        cCompiler = new CCompiler( this );
        cppCompiler = new CppCompiler( this );
        assembler = new Assembler( this );
        cLinker = new CLinker( this );
        cppLinker = new CppLinker( this );
        archiver = new Archiver( this );
    }

    public static boolean matches( Configuration configuration ) {

        if ( configuration.getId().startsWith( ID ) ) {
            return true;
        }
        return configuration.getId().startsWith( ID_OSX );
    }

    @Override
    public String getId() {

        return toolChain.getId();
    }

    @Override
    public String getName() {

        return toolChain.getName();
    }

    @Override
    public Options getOptions() {

        return options;
    }

    @Override
    public Builder getBuilder() {

        return builder;
    }

    public CCompiler getCCompiler() {

        return cCompiler;
    }

    public CppCompiler getCppCompiler() {

        return cppCompiler;
    }

    public Assembler getAssembler() {

        return assembler;
    }

    public CLinker getCLinker() {

        return cLinker;
    }

    public CppLinker getCppLinker() {

        return cppLinker;
    }

    public Archiver getArchiver() {

        return archiver;
    }

    public static final class Builder implements ToolChain.Builder {

        private final GnuToolChain toolChain;
        private final generated.org.eclipse.cdt.ToolChain.Builder builder;

        private Builder(GnuToolChain toolChain) {

            this.toolChain = toolChain;
            builder = toolChain.toolChain.getBuilder();
        }

        @Override
        public String getId() {

            return builder.getId();
        }

        @Override
        public String getName() {

            return builder.getName();
        }

        @Override
        public String getBuildPath() {

            return CProjectService.getBuildPath( toolChain.configuration, builder );
        }

        @Override
        public boolean isMakefile() {

            return CProjectService.isMakefileBuilder( builder );
        }
    }

    private static abstract class Tool implements ToolChain.Tool {

        private static final String ID = "cdt.managedbuild.tool.gnu";

        protected final GnuToolChain toolChain;
        protected final generated.org.eclipse.cdt.ToolChain.Tool tool;

        protected Tool(GnuToolChain toolChain) {

            this.toolChain = toolChain;
            tool = CProjectService.getTool( toolChain.toolChain, getId() );
        }

        protected String getToolOptionValue( String superClass ) {

            return CProjectService.getToolOptionValue( tool, superClass );
        }

        protected String[] getToolOptionValues( String superClass ) {

            return CProjectService.getToolOptionValues( tool, superClass );
        }

        protected abstract String getOptionsId();
    }

    public static class CCompiler extends Tool {

        private static final String ID = Tool.ID + ".c.compiler.base";
        private static final String OPTIONS = "gnu.c.compiler.option";

        private CCompiler(GnuToolChain toolChain) {

            super( toolChain );
        }

        @Override
        public String getId() {

            return ID;
        }

        @Override
        protected String getOptionsId() {

            return OPTIONS;
        }

        public String[] getIncludePaths() {

            return getToolOptionValues( getOptionsId() + ".include.paths" );
        }

        public String[] getDefines() {

            return getToolOptionValues( getOptionsId() + ".defines" );
        }

        public boolean getVerbose() {

            String value = getToolOptionValue( getOptionsId() + ".other.verbose" );
            return Boolean.parseBoolean( value );
        }

        public String getOtherOptions() {

            return getToolOptionValue( getOptionsId() + ".other.other" );
        }

        public int getOptLevel() {

            String optLevel = getOptionsId() + ".optimization.level";
            String value = getToolOptionValue( optLevel );

            if ( value == null || value.length() == 0 ) {
                return -1;
            }

            value = value.substring( "gnu.cpp.compiler.optimization.level.".length() );

            if ( "none".equals( value ) ) {
                return 0;
            }
            else if ( "optimize".equals( value ) ) {
                return 1;
            }
            else if ( "more".equals( value ) ) {
                return 2;
            }
            else if ( "max".equals( value ) ) {
                return 3;
            }

            try {
                return Integer.parseInt( value );
            }
            catch (NumberFormatException e) {
                return -1;
            }
        }

        public int getDebugLevel() {

            String debugLevel = getOptionsId() + ".debugging.level";
            String value = getToolOptionValue( debugLevel );

            if ( value == null || value.length() == 0 ) {
                return -1;
            }

            value = value.substring( "gnu.cpp.compiler.debugging.level.".length() );

            if ( "none".equals( value ) ) {
                return 0;
            }
            else if ( "minimal".equals( value ) ) {
                return 1;
            }
            else if ( "default".equals( value ) ) {
                return 2;
            }
            else if ( "max".equals( value ) ) {
                return 3;
            }

            try {
                return Integer.parseInt( value );
            }
            catch (NumberFormatException e) {
                return -1;
            }
        }
    }

    public static final class CppCompiler extends CCompiler {

        private static final String ID = Tool.ID + ".cpp.compiler.base";
        private static final String OPTIONS = "gnu.cpp.compiler.option";

        private CppCompiler(GnuToolChain toolChain) {

            super( toolChain );
        }

        @Override
        public String getId() {

            return ID;
        }

        @Override
        protected String getOptionsId() {

            return OPTIONS;
        }
    }

    public static final class Assembler extends Tool {

        private static final String ID = Tool.ID + ".assembler.base";
        private static final String OPTIONS = "gnu.both.asm.option";

        private Assembler(GnuToolChain toolChain) {

            super( toolChain );
        }

        @Override
        public String getId() {

            return ID;
        }

        @Override
        protected String getOptionsId() {

            return OPTIONS;
        }

        public boolean getNoWarn() {

            String value = getToolOptionValue( OPTIONS + ".warnings.nowarn" );
            return Boolean.parseBoolean( value );
        }
    }

    public static class CLinker extends Tool {

        private static final String ID = Tool.ID + ".c.linker.base";
        private static final String OPTIONS = "gnu.c.link.option";

        private CLinker(GnuToolChain toolChain) {

            super( toolChain );
        }

        @Override
        public String getId() {

            return ID;
        }

        @Override
        protected String getOptionsId() {

            return OPTIONS;
        }

        public boolean getStrip() {

            String value = getToolOptionValue( OPTIONS + ".debugging.level" );
            return Boolean.parseBoolean( value );
        }
    }

    public static final class CppLinker extends CLinker {

        private static final String ID = Tool.ID + ".cpp.linker.base";
        private static final String OPTIONS = "gnu.cpp.link.option";

        private CppLinker(GnuToolChain toolChain) {

            super( toolChain );
        }

        @Override
        public String getId() {

            return ID;
        }

        @Override
        protected String getOptionsId() {

            return OPTIONS;
        }
    }

    public static final class Archiver extends Tool {

        private static final String ID = Tool.ID + ".archiver.base";

        private Archiver(GnuToolChain toolChain) {

            super( toolChain );
        }

        @Override
        public String getId() {

            return ID;
        }

        @Override
        protected String getOptionsId() {

            return null;
        }
    }

    public static final class Options implements ToolChain.Options {

        private final GnuToolChain toolChain;

        private Options(GnuToolChain toolChain) {

            this.toolChain = toolChain;
        }

        @Override
        public String getId() {

            return ID;
        }
    }
}
