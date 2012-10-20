/**
 * Copyright (c) 2010-2012 Martin M Reed
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
package org.hardisonbrewing.maven.cxx.xcode;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.cli.CommandLineUtils.StringStreamConsumer;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;

/**
 * @goal xcode-install-codesign-certificate
 * @phase generate-resources
 */
public class InstallCodesignCertificateMojo extends JoJoMojoImpl {

    private static final String SED_SERIAL_NUMBER = "/Serial Number/s/^.*\\: *\\(.*\\)/\\1/p";
    private static final String SED_IDENTITY = "/Subject Name/,/Common Name/s/^ *Common Name *\\: *\\(.*\\)/\\1/p";

    /**
     * @parameter
     */
    public String codesignCertificate;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        if ( codesignCertificate == null ) {
            getLog().info( "Codesign certificate not specified, skipping." );
            return;
        }

        File certificateFile = CodesignCertificateService.getCertificateFile( codesignCertificate );

        storeIdentity( certificateFile );

        String serialNumber = getSerialNumber( certificateFile );
        if ( hasSerialNumber( serialNumber ) ) {
            getLog().info( "Codesign certificate already installed, skipping." );
            return;
        }

        importCertificateFile( certificateFile );
    }

    private void storeIdentity( File file ) {

        String identity = getIdentity( file );

        getLog().info( XCodeService.CODE_SIGN_IDENTITY + ": " + identity );

        Properties properties = PropertiesService.getXCodeProperties();
        properties.put( XCodeService.CODE_SIGN_IDENTITY, identity );
        PropertiesService.storeXCodeProperties( properties );
    }

    private void importCertificateFile( File file ) {

        List<String> cmd = new LinkedList<String>();
        cmd.add( "certtool" );
        cmd.add( "i" );
        cmd.add( file.getAbsolutePath() );
        cmd.add( "d" );
        execute( cmd );
    }

    private String getSerialNumber( File file ) {

        List<String> cmd = new LinkedList<String>();
        cmd.add( "certtool" );
        cmd.add( "d" );
        cmd.add( file.getAbsolutePath() );
        cmd.add( "|" );
        cmd.add( "sed" );
        cmd.add( "-n" );
        cmd.add( SED_SERIAL_NUMBER );

        StringStreamConsumer streamConsumer = new StringStreamConsumer();
        execute( cmd, streamConsumer, null );

        return streamConsumer.getOutput().trim();
    }

    private String getIdentity( File file ) {

        List<String> cmd = new LinkedList<String>();
        cmd.add( "certtool" );
        cmd.add( "d" );
        cmd.add( file.getAbsolutePath() );
        cmd.add( "|" );
        cmd.add( "sed" );
        cmd.add( "-n" );
        cmd.add( SED_IDENTITY );

        StringStreamConsumer streamConsumer = new StringStreamConsumer();
        execute( cmd, streamConsumer, null );

        return streamConsumer.getOutput().trim();
    }

    private boolean hasSerialNumber( String serialNumber ) {

        List<String> cmd = new LinkedList<String>();
        cmd.add( "certtool" );
        cmd.add( "y" );
        cmd.add( "|" );
        cmd.add( "sed" );
        cmd.add( "-n" );
        cmd.add( SED_SERIAL_NUMBER.replace( "Serial Number", serialNumber ) );

        StringStreamConsumer streamConsumer = new StringStreamConsumer();
        execute( cmd, streamConsumer, null );

        return serialNumber.equals( streamConsumer.getOutput().trim() );
    }
}
