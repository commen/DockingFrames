/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Benjamin Sigg
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
package bibliothek.gui.dock.extension.css.animation.types;

import bibliothek.gui.dock.extension.css.CssProperty;
import bibliothek.gui.dock.extension.css.CssPropertyContainer;
import bibliothek.gui.dock.extension.css.CssPropertyContainerListener;
import bibliothek.gui.dock.extension.css.animation.AnimatedCssProperty;
import bibliothek.gui.dock.extension.css.animation.AnimatedCssPropertyCallback;

/**
 * This {@link AnimatedCssProperty} handles objects of type {@link CssPropertyContainer}, it automatically requests and
 * handles the sub-properties. 
 * @author Benjamin Sigg
 * @param <T> the type of object animated by this property
 */
public abstract class AbstractAnimatedCssProperty<T extends CssPropertyContainer> implements AnimatedCssProperty<T> {
	private AnimatedCssPropertyCallback<T> callback;
	
	private T source;
	private T target;
	
	private double transition = 0;
	
	private SourceListener sourceListener;
	private TargetListener targetListener;
	
	@Override
	public void setCallback( AnimatedCssPropertyCallback<T> callback ){
		this.callback = callback;
	}
	
	/**
	 * Gets the current callback.
	 * @return the callback used to forward new values
	 */
	public AnimatedCssPropertyCallback<T> getCallback(){
		return callback;
	}

	@Override
	public void setSource( T source ){
		if( this.source != null ){
			source.removePropertyContainerListener( sourceListener );
			sourceListener.removeAll();
			sourceListener = null;
		}
		this.source = source;
		if( source != null ){
			sourceListener = new SourceListener();
			source.addPropertyContainerListener( sourceListener );
			sourceListener.addAll();
		}
		update();
	}

	/**
	 * Gets the current source value.
	 * @return the last value from {@link #setSource(CssPropertyContainer)}
	 */
	public T getSource(){
		return source;
	}
	
	@Override
	public void setTarget( T target ){
		if( this.target != null ){
			target.removePropertyContainerListener( targetListener );
			targetListener.removeAll();
			targetListener = null;
		}
		this.target = target;
		if( target != null ){
			targetListener = new TargetListener();
			target.addPropertyContainerListener( targetListener );
			targetListener.addAll();
		}
		update();
	}
	
	/**
	 * Gets the current target value.
	 * @return the last value from {@link #setTarget(CssPropertyContainer)}
	 */
	public T getTarget(){
		return target;
	}
	
	@Override
	public void setTransition( double transition ){
		this.transition = transition;
		update();
	}
	
	/**
	 * Gets the current progress of the transition.
	 * @return a value between <code>0</code> and <code>1</code>
	 */
	public double getTransition(){
		return transition;
	}

	@Override
	public void step( int delay ){
		update();
	}
	
	/**
	 * Called every time one of the properties of this {@link AbstractAnimatedCssProperty} is changed.
	 */
	protected abstract void update();
	

	/**
	 * Ensures that all the sub-{@link CssProperty}s of {@link AbstractAnimatedCssProperty#source} are
	 * recognized as dependency.
	 * @author Benjamin Sigg
	 */
	private class SourceListener implements CssPropertyContainerListener{
		@Override
		public void propertyAdded( CssPropertyContainer source, String key, CssProperty<?> property ){
			callback.addSourceDependency( key, property );
		}
		
		@Override
		public void propertyRemoved( CssPropertyContainer source, String key, CssProperty<?> property ){
			callback.removeSourceDependency( key );
		}
		
		private void addAll(){
			for( String key : source.getPropertyKeys() ){
				propertyAdded( source, key, source.getProperty( key ) );
			}
		}
		
		private void removeAll(){
			for( String key : source.getPropertyKeys() ){
				propertyRemoved( source, key, source.getProperty( key ) );
			}
		}
	}

	/**
	 * Ensures that all the sub-{@link CssProperty}s of {@link AbstractAnimatedCssProperty#target} are
	 * considered part of this animation.
	 * @author Benjamin Sigg
	 */
	private class TargetListener implements CssPropertyContainerListener{
		@Override
		public void propertyAdded( CssPropertyContainer source, String key, CssProperty<?> property ){
			callback.addTargetDependency( key, property );
		}
		
		@Override
		public void propertyRemoved( CssPropertyContainer source, String key, CssProperty<?> property ){
			callback.removeTargetDependency( key );
		}
		
		private void addAll(){
			for( String key : target.getPropertyKeys() ){
				propertyAdded( target, key, target.getProperty( key ) );
			}
		}
		
		private void removeAll(){
			for( String key : target.getPropertyKeys() ){
				propertyRemoved( target, key, target.getProperty( key ) );
			}
		}		
	}
}
