/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2010 Benjamin Sigg
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
package bibliothek.gui.dock.control.focus;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingUtilities;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.control.DockRegister;
import bibliothek.gui.dock.event.DockRegisterAdapter;
import bibliothek.gui.dock.event.DockRegisterListener;

/**
 * The {@link DefaultFocusStrategy} keeps track of the last focused {@link Component} of any
 * {@link Dockable} that is registered at a {@link DockController}.
 * @author Benjamin Sigg
 */
public class DefaultFocusStrategy implements FocusStrategy{
	/** the controller to monitor for new {@link Dockable}s */
	private DockController controller;
	
	/** all the currently observed {@link Dockable}s */
	private Map<Component, Tracker> trackers = new HashMap<Component, Tracker>(); 
	
	/** is informed about new {@link Dockable}s */
	private DockRegisterListener listener = new DockRegisterAdapter(){
		public void dockableRegistered( DockController controller, Dockable dockable ){
			add( dockable );
		}
		
		public void dockableUnregistered( DockController controller, Dockable dockable ){
			remove( dockable );
		}
	};
	
	public DefaultFocusStrategy( DockController controller ){
		this.controller = controller;
	}
	
	public Component getFocusComponent( Dockable dockable ){
		Tracker tracker = trackers.get( dockable.getComponent() );
		if( tracker == null ){
			return null;
		}
		return tracker.getLastFocused();
	}
	
	public void bind(){
		DockRegister register = controller.getRegister();
		register.addDockRegisterListener( listener );
		for( int i = 0, n = register.getDockableCount(); i<n; i++ ){
			add( register.getDockable( i ));
		}
	}
	
	public void unbind(){
		controller.getRegister().removeDockRegisterListener( listener );
		for( Tracker tracker : trackers.values() ){
			tracker.destroy();
		}
		trackers.clear();
	}
	
	private void add( Dockable dockable ){
		Tracker tracker = new Tracker( dockable );
		for( Tracker other : trackers.values() ){
			if( SwingUtilities.isDescendingFrom( dockable.getComponent(), other.dockable.getComponent() )){
				other.remove( dockable.getComponent() );	
			}
		}
		trackers.put( dockable.getComponent(), tracker );
	}
	
	private void remove( Dockable dockable ){
		Tracker tracker = trackers.remove( dockable.getComponent() );
		if( tracker != null ){
			tracker.destroy();
		}
	}
	
	private class Tracker extends FocusTracker{
		private Dockable dockable;
		
		public Tracker( Dockable dockable ){
			super( dockable.getComponent() );
			this.dockable = dockable;
		}
		
		@Override
		protected void add( Component component ){
			if( !trackers.containsKey( component )){
				super.add( component );
			}
		}
	}
}
