/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2009 Benjamin Sigg
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */
package bibliothek.gui.dock.common.intern;

import bibliothek.gui.DockStation;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.CStation;

/**
 * An abstract implementation of {@link CStation} that can be docked like a {@link CDockable}.
 * @author Benjamin Sigg
 *
 */
public abstract class AbstractDockableCStation extends AbstractCDockable implements CStation{
    private CLocation location;
    private String id;
    private DockStation station;
    
    /**
     * Creates a new station.
     * @param station the internal representation of this station
     * @param id the unique id of this station
     * @param location a location that points directly to this station
     * @param dockable how this station appears as dockable
     */
    public AbstractDockableCStation( DockStation station, String id, CLocation location, CommonDockable dockable ){
    	super( null );
    	init( station, id, location, dockable );
    }
    
    /**
     * Creates a new station but does not yet initialize its fields. Subclasses
     * should call {@link #init(DockStation, String, CLocation)}.
     */
    protected AbstractDockableCStation(){
    	super( null );
    }
    
    /**
     * Initializes the fields of this station.
     * @param station the internal representation of this station
     * @param id the unique id of this station
     * @param location a location that points directly to this station
     * @param dockable how this station appears as dockable
     */
    protected void init( DockStation station, String id, CLocation location, CommonDockable dockable ){
    	if( station == null )
    		throw new IllegalArgumentException( "station must not be null" );
    	
    	if( id == null )
    		throw new IllegalArgumentException( "id must not be null" );
    	
    	if( location == null )
    		throw new IllegalArgumentException( "location must not be null" );
    	
    	super.init( dockable );
    	
        this.station = station;
        this.id = id;
        this.location = location;	
    }
    
    public CLocation getStationLocation() {
        return location;
    }

    public String getUniqueId() {
        return id;
    }

    public DockStation getStation() {
        return station;
    }

    public void setControl( CControlAccess access ) {
    	CControlAccess control = getControl();
    	super.setControl( access );
        if( control != access ){
            if( control != null )
                uninstall( control );
            
            control = access;
            if( control != null )
                install( control );
        }
    }
    
    public boolean isWorkingArea() {
        return false;
    }

    public CDockable asDockable() {
        return null;
    }
    
    /**
     * Called when this station is added to a {@link CControl}.
     * @param access access to the internals of the new owner
     */
    protected abstract void install( CControlAccess access );
    
    /**
     * Called when this station is removed from a {@link CControl}.
     * @param access access to the internals of the old owner
     */
    protected abstract void uninstall( CControlAccess access );
}
