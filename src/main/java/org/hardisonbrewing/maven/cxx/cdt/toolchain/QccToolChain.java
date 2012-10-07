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

    private final Options options;
    private final Builder builder;

    private final Compiler compiler;
    private final Assembler assembler;
    private final Linker linker;
    private final Archiver archiver;

    private final Configuration configuration;
    private final generated.org.eclipse.cdt.ToolChain toolChain;

    public QccToolChain(Configuration configuration) {

        // these need to be set first because Tools,
        // Options and Builder may try to reference toolChain
        this.configuration = configuration;
        toolChain = CProjectService.getToolChain( configuration );

        options = new Options( this );
        builder = new Builder( this );

        compiler = new Compiler( this );
        assembler = new Assembler( this );
        linker = new Linker( this );
        archiver = new Archiver( this );
    }

    public static boolean matches( Configuration configuration ) {

        return configuration.getId().startsWith( ID );
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

    public Compiler getCompiler() {

        return compiler;
    }

    public Assembler getAssembler() {

        return assembler;
    }

    public Linker getLinker() {

        return linker;
    }

    public Archiver getArchiver() {

        return archiver;
    }

    public static final class Builder implements ToolChain.Builder {

        private final Configuration configuration;
        private final generated.org.eclipse.cdt.ToolChain.Builder builder;

        private Builder(QccToolChain toolChain) {

            configuration = toolChain.configuration;
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

            return CProjectService.getBuildPath( configuration, builder );
        }

        @Override
        public boolean isMakefile() {

            return CProjectService.isMakefileBuilder( builder );
        }
    }

    private static abstract class Tool implements ToolChain.Tool {

        private static final String ID = QccToolChain.ID + ".tool";

        private final generated.org.eclipse.cdt.ToolChain.Tool tool;

        protected Tool(QccToolChain toolChain) {

            tool = CProjectService.getTool( toolChain.toolChain, getId() );
        }

        protected abstract String getOptionsId();

        protected String getToolOptionValue( String superClass ) {

            return CProjectService.getToolOptionValue( tool, superClass );
        }

        protected String[] getToolOptionValues( String superClass ) {

            return CProjectService.getToolOptionValues( tool, superClass );
        }

        public int getOptLevel() {

            String superClass = getOptionsId();
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

            String superClass = getOptionsId();
            String value = getToolOptionValue( superClass + ".debug" );
            return Boolean.parseBoolean( value );
        }

        public boolean useCodeCoverage() {

            String superClass = getOptionsId();
            String value = getToolOptionValue( superClass + ".coverage" );
            return Boolean.parseBoolean( value );
        }

        public boolean useProfile() {

            String superClass = getOptionsId();
            String value = getToolOptionValue( superClass + ".profile2" );
            return Boolean.parseBoolean( value );
        }

        public boolean usePie() {

            String superClass = getOptionsId();
            String value = getToolOptionValue( superClass + ".pie" );
            return Boolean.parseBoolean( value );
        }

        public boolean useSecurity() {

            String superClass = getOptionsId();
            String value = getToolOptionValue( superClass + ".security" );
            return Boolean.parseBoolean( value );
        }
    }

    public static final class Compiler extends Tool {

        private static final String ID = Tool.ID + ".compiler";
        private static final String OPTIONS = Options.ID + ".compiler";

        private Compiler(QccToolChain toolChain) {

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

        public boolean isDebug() {

            // superClass for compiler debug is different... probably a typo
            String superClass = OPTIONS + ".compile";
            String value = getToolOptionValue( superClass + ".debug" );
            return Boolean.parseBoolean( value );
        }

        public String[] getIncludePaths() {

            return getToolOptionValues( OPTIONS + ".includePath" );
        }

        public String[] getDefines() {

            return getToolOptionValues( OPTIONS + ".defines" );
        }
    }

    public static final class Assembler extends Tool {

        private static final String ID = Tool.ID + ".assembler";
        private static final String OPTIONS = Options.ID + ".assembler";

        private Assembler(QccToolChain toolChain) {

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

    public static final class Linker extends Tool {

        private static final String ID = Tool.ID + ".linker";
        private static final String OPTIONS = Options.ID + ".linker";

        private Linker(QccToolChain toolChain) {

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

        public String[] getLibraries() {

            return getToolOptionValues( OPTIONS + ".libraries" );
        }

        public String[] getLibraryPaths() {

            return getToolOptionValues( OPTIONS + ".libraryPaths" );
        }
    }

    public static final class Archiver extends Tool {

        private static final String ID = Tool.ID + ".archiver";
        private static final String OPTIONS = Options.ID + ".archiver";

        private Archiver(QccToolChain toolChain) {

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

    public static final class Options implements ToolChain.Options {

        private static final String ID = QccToolChain.ID + ".option";
        private static final String CPU = ID + ".cpu";
        private static final String GEN_CPU = ID + ".gen.cpu";

        private final generated.org.eclipse.cdt.ToolChain toolChain;

        private Options(QccToolChain toolChain) {

            this.toolChain = toolChain.toolChain;
        }

        @Override
        public String getId() {

            return ID;
        }

        public String getPlatform() {

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
    }
}
