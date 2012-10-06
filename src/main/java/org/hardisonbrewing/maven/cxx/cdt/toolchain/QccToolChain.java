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

import java.util.ArrayList;
import java.util.List;

import org.hardisonbrewing.maven.cxx.cdt.CProjectService;
import org.hardisonbrewing.maven.cxx.qnx.QnxService;

public final class QccToolChain implements ToolChain {

    public static final String ID = "com.qnx.qcc";

    private final Tools tools;
    private final Options options;
    private final Builder builder;

    private final Configuration configuration;
    private final generated.org.eclipse.cdt.ToolChain toolChain;

    public QccToolChain(Configuration configuration) {

        // these need to be set first because Tools,
        // Options and Builder may try to reference toolChain
        this.configuration = configuration;
        toolChain = CProjectService.getToolChain( configuration );

        tools = new Tools( this );
        options = new Options( this );
        builder = new Builder( this );
    }

    public static boolean matches( Configuration configuration ) {

        return configuration.getId().startsWith( ID );
    }

    @Override
    public Tools getTools() {

        return tools;
    }

    @Override
    public Options getOptions() {

        return options;
    }

    @Override
    public String getName() {

        return toolChain.getName();
    }

    @Override
    public String getId() {

        return toolChain.getId();
    }

    @Override
    public Builder getBuilder() {

        return builder;
    }

    public static final class Builder implements ToolChain.Builder {

        private final QccToolChain toolChain;
        private final generated.org.eclipse.cdt.ToolChain.Builder builder;

        private Builder(QccToolChain toolChain) {

            this.toolChain = toolChain;
            builder = toolChain.toolChain.getBuilder();
        }

        @Override
        public String getId() {

            return builder.getId();
        }

        @Override
        public String getName() {

            return null;
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

    public static final class Tools {

        public static final String ID = QccToolChain.ID + ".tool";

        private final Tool.Compiler compiler;
        private final Tool.Assembler assembler;
        private final Tool.Linker linker;
        private final Tool.Archiver archiver;

        private Tools(QccToolChain toolChain) {

            compiler = new Tool.Compiler( toolChain );
            assembler = new Tool.Assembler( toolChain );
            linker = new Tool.Linker( toolChain );
            archiver = new Tool.Archiver( toolChain );
        }

        public Tool.Compiler getCompiler() {

            return compiler;
        }

        public Tool.Assembler getAssembler() {

            return assembler;
        }

        public Tool.Linker getLinker() {

            return linker;
        }

        public Tool.Archiver getArchiver() {

            return archiver;
        }
    }

    public static abstract class Tool implements ToolChain.Tool {

        public static final String ID = QccToolChain.ID + ".tool";

        protected final generated.org.eclipse.cdt.ToolChain.Tool tool;

        protected Tool(QccToolChain toolChain) {

            tool = CProjectService.getTool( toolChain.toolChain, getId() );
        }

        protected String getToolOptionValue( String superClass ) {

            return CProjectService.getToolOptionValue( tool, superClass );
        }

        public int getOptLevel() {

            String superClass = getOptions().getId();
            String optLevel = superClass + ".optlevel";
            String value = getToolOptionValue( optLevel );

            if ( value == null ) {
                return -1;
            }

            value = value.substring( ( optLevel + "." ).length() );

            try {
                return Integer.parseInt( value );
            }
            catch (NumberFormatException e) {
                return -1;
            }
        }

        public boolean isDebug() {

            String superClass = getOptions().getId();
            String value = getToolOptionValue( superClass + ".debug" );
            return Boolean.parseBoolean( value );
        }

        public boolean useCodeCoverage() {

            String superClass = getOptions().getId();
            String value = getToolOptionValue( superClass + ".coverage" );
            return Boolean.parseBoolean( value );
        }

        public boolean useProfile() {

            String superClass = getOptions().getId();
            String value = getToolOptionValue( superClass + ".profile2" );
            return Boolean.parseBoolean( value );
        }

        public boolean usePie() {

            String superClass = getOptions().getId();
            String value = getToolOptionValue( superClass + ".pie" );
            return Boolean.parseBoolean( value );
        }

        public boolean useSecurity() {

            String superClass = getOptions().getId();
            String value = getToolOptionValue( superClass + ".security" );
            return Boolean.parseBoolean( value );
        }

        public static final class Compiler extends Tool {

            public static final String ID = Tool.ID + ".compiler";

            private final Options.Compiler options;

            private Compiler(QccToolChain toolChain) {

                super( toolChain );

                options = new Options.Compiler( toolChain );
            }

            public String getId() {

                return ID;
            }

            @Override
            public Options.Compiler getOptions() {

                return options;
            }

            public boolean isDebug() {

                // superClass for compiler debug is different... probably a typo
                String superClass = Options.ID + ".compile";
                String value = getToolOptionValue( superClass + ".debug" );
                return Boolean.parseBoolean( value );
            }
        }

        public static final class Assembler extends Tool {

            public static final String ID = Tool.ID + ".assembler";

            private final Options.Assembler options;

            private Assembler(QccToolChain toolChain) {

                super( toolChain );

                options = new Options.Assembler();
            }

            public String getId() {

                return ID;
            }

            @Override
            public Options.Assembler getOptions() {

                return options;
            }
        }

        public static final class Linker extends Tool {

            public static final String ID = Tool.ID + ".linker";

            private final Options.Linker options;

            private Linker(QccToolChain toolChain) {

                super( toolChain );

                options = new Options.Linker( toolChain );
            }

            public String getId() {

                return ID;
            }

            @Override
            public Options.Linker getOptions() {

                return options;
            }
        }

        public static final class Archiver extends Tool {

            public static final String ID = Tool.ID + ".archiver";

            private final Options.Archiver options;

            private Archiver(QccToolChain toolChain) {

                super( toolChain );

                options = new Options.Archiver();
            }

            public String getId() {

                return ID;
            }

            @Override
            public Options.Archiver getOptions() {

                return options;
            }
        }
    }

    public static final class Options implements ToolChain.Options {

        public static final String ID = QccToolChain.ID + ".option";
        public static final String CPU = ID + ".cpu";
        public static final String GEN_CPU = ID + ".gen.cpu";

        private final QccToolChain toolChain;

        private Options(QccToolChain toolChain) {

            this.toolChain = toolChain;
        }

        @Override
        public String getId() {

            return ID;
        }

        public String getPlatform() {

            generated.org.eclipse.cdt.ToolChain toolChain = this.toolChain.toolChain;
            String value = CProjectService.getToolChainOptionValue( toolChain, Options.CPU );
            return value.substring( Options.GEN_CPU.length() + 1 );
        }

        public String getCompilerPlatform() {

            String compiler = QnxService.getDefaultCompiler();
            String version = QnxService.getDefaultCompilerVersion( compiler );
            String platform = getPlatform();

            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append( version );
            stringBuffer.append( "," );
            stringBuffer.append( compiler );
            stringBuffer.append( "_nto" );
            stringBuffer.append( getCpu( platform ) );

            String[] cpuVariant = getCpuVariant( platform );
            if ( cpuVariant != null ) {
                for (String variant : cpuVariant) {
                    stringBuffer.append( variant );
                }
            }

            String endian = getEndian( platform );
            if ( endian != null ) {
                stringBuffer.append( endian );
            }

            return stringBuffer.toString();
        }

        public String getCpu( String platform ) {

            int hyphen = platform.indexOf( '-' );
            if ( hyphen >= 0 ) {
                platform = platform.substring( 0, hyphen );
            }

            String endian = getEndian( platform );
            if ( endian != null ) {
                platform = platform.substring( 0, platform.length() - endian.length() );
            }

            return platform;
        }

        public String getEndian( String platform ) {

            int hyphen = platform.indexOf( '-' );
            if ( hyphen >= 0 ) {
                platform = platform.substring( 0, hyphen );
            }
            if ( platform.endsWith( "be" ) ) {
                return "be";
            }
            if ( platform.endsWith( "le" ) ) {
                return "le";
            }
            return null;
        }

        public String[] getCpuVariant( String platform ) {

            List<String> result = new ArrayList<String>( 2 );

            int start = 0;
            while (( start = platform.indexOf( '-', start ) ) != -1) {

                start++;
                int end = platform.indexOf( '-', start );

                if ( end == -1 ) {
                    result.add( platform.substring( start ) );
                    break;
                }

                result.add( platform.substring( start, end ) );

                start = end + 1;
            }

            if ( result.isEmpty() ) {
                return null;
            }

            String[] _result = new String[result.size()];
            result.toArray( _result );
            return _result;
        }

        public static final class Compiler implements ToolChain.Options {

            public static final String ID = Options.ID + ".compiler";
            public static final String DEFINES = ID + ".defines";
            public static final String INCLUDE_PATHS = ID + ".includePath";

            private final QccToolChain toolChain;

            private Compiler(QccToolChain toolChain) {

                this.toolChain = toolChain;
            }

            @Override
            public String getId() {

                return ID;
            }

            public String[] getIncludePaths() {

                generated.org.eclipse.cdt.ToolChain toolChain = this.toolChain.toolChain;
                generated.org.eclipse.cdt.ToolChain.Tool tool = CProjectService.getTool( toolChain, Tool.Compiler.ID );
                return CProjectService.getToolOptionValues( tool, INCLUDE_PATHS );
            }

            public String[] getDefines() {

                generated.org.eclipse.cdt.ToolChain toolChain = this.toolChain.toolChain;
                generated.org.eclipse.cdt.ToolChain.Tool tool = CProjectService.getTool( toolChain, Tool.Compiler.ID );
                return CProjectService.getToolOptionValues( tool, DEFINES );
            }
        }

        public static final class Assembler implements ToolChain.Options {

            public static final String ID = Options.ID + ".assembler";

            private Assembler() {

                // hide constructor
            }

            @Override
            public String getId() {

                return ID;
            }
        }

        public static final class Linker implements ToolChain.Options {

            public static final String ID = Options.ID + ".linker";
            public static final String LIBRARIES = ID + ".libraries";
            public static final String LIBRARY_PATHS = ID + ".libraryPaths";

            private final QccToolChain toolChain;

            private Linker(QccToolChain toolChain) {

                this.toolChain = toolChain;
            }

            @Override
            public String getId() {

                return ID;
            }

            public String[] getLibraries() {

                generated.org.eclipse.cdt.ToolChain toolChain = this.toolChain.toolChain;
                generated.org.eclipse.cdt.ToolChain.Tool tool = CProjectService.getTool( toolChain, Tool.Linker.ID );
                return CProjectService.getToolOptionValues( tool, LIBRARIES );
            }

            public String[] getLibraryPaths() {

                generated.org.eclipse.cdt.ToolChain toolChain = this.toolChain.toolChain;
                generated.org.eclipse.cdt.ToolChain.Tool tool = CProjectService.getTool( toolChain, Tool.Linker.ID );
                return CProjectService.getToolOptionValues( tool, LIBRARY_PATHS );
            }
        }

        public static final class Archiver implements ToolChain.Options {

            public static final String ID = Options.ID + ".archiver";

            private Archiver() {

                // hide constructor
            }

            @Override
            public String getId() {

                return ID;
            }
        }
    }
}
