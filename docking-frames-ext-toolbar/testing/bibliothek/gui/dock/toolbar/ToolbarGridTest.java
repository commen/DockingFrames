package bibliothek.gui.dock.toolbar;

import static org.junit.Assert.*;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.dockable.AbstractDockable;
import bibliothek.gui.dock.station.support.ConvertedPlaceholderListItem;
import bibliothek.gui.dock.station.support.PlaceholderList;
import bibliothek.gui.dock.station.support.PlaceholderListItem;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.gui.dock.station.support.PlaceholderStrategyListener;
import bibliothek.gui.dock.station.toolbar.group.ToolbarColumn;
import bibliothek.gui.dock.station.toolbar.group.ToolbarColumnListener;
import bibliothek.gui.dock.station.toolbar.group.ToolbarColumnModel;
import bibliothek.gui.dock.station.toolbar.group.ToolbarColumnModelListener;
import bibliothek.gui.dock.station.toolbar.layout.DockablePlaceholderToolbarGrid;
import bibliothek.gui.dock.station.toolbar.layout.GridPlaceholderList;
import bibliothek.gui.dock.station.toolbar.layout.GridPlaceholderList.Column;
import bibliothek.gui.dock.station.toolbar.layout.PlaceholderToolbarGridConverter;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.icon.DockIcon;
import bibliothek.util.Path;

public class ToolbarGridTest {
	private TestStory story;

	@Before
	public void setup(){
		story = new TestStory();
	}

	@After
	public void clear(){
		story = null;
	}

	@Test
	public void testOneColumn(){
		story.insert( 0, 0, null );
		story.insert( 0, 1, null );
		story.insert( 0, 2, "a" );

		story.assertCell( 0, 0, null );
		story.assertCell( 0, 1, null );
		story.assertCell( 0, 2, "a" );
	}

	@Test
	public void testOneColumnAddRemove(){
		story.insert( 0, 0, null );
		story.insert( 0, 1, null );
		story.insert( 0, 2, "a" );
		story.remove( 0, 1 );

		story.assertCell( 0, 0, null );
		story.assertCell( 0, 1, "a" );
	}

	@Test
	public void testOneColumnAddRemovePlaceholder(){
		story.insert( 0, 0, null );
		story.insert( 0, 1, "a" );
		story.insert( 0, 2, null );

		story.remove( 0, 1 );
		story.insert( "a" );

		story.assertCell( 0, 0, null );
		story.assertCell( 0, 1, "a" );
		story.assertCell( 0, 2, null );
	}

	@Test
	public void testMultiColumn(){
		story.insert( 0, 0, null );
		story.insert( 0, 1, null );
		story.insert( 0, 2, null );

		story.insert( 1, 0, null );
		story.insert( 1, 1, null );

		story.assertCell( 0, 0, null );
		story.assertCell( 0, 1, null );
		story.assertCell( 0, 2, null );

		story.assertCell( 1, 0, null );
		story.assertCell( 1, 1, null );
	}

	@Test
	public void testColumnAddRemovePlaceholder(){
		story.insert( 0, 0, null );
		story.insert( 0, 1, null );
		story.insert( 0, 2, null );

		story.insert( 1, 0, "a" );
		story.insert( 1, 1, "b" );

		story.insert( 2, 0, null );
		story.insert( 2, 1, null );

		story.remove( 1, 0 );
		story.remove( 1, 0 );

		story.insert( "b" );
		story.insert( "a" );

		story.assertCell( 0, 0, null );
		story.assertCell( 0, 1, null );
		story.assertCell( 0, 2, null );

		story.assertCell( 1, 0, "a" );
		story.assertCell( 1, 1, "b" );

		story.assertCell( 2, 0, null );
		story.assertCell( 2, 1, null );
	}

	@Test
	public void testNewPosition(){
		story.insert( 0, 0, "a" );
		story.insert( 0, 1, "b" );
		story.insert( 0, 2, "c" );

		story.insert( 1, 0, "d" );
		story.insert( 1, 1, "e" );
		story.insert( 1, 2, "f" );

		story.insert( 2, 0, "g" );
		story.insert( 2, 1, "h" );
		story.insert( 2, 2, "i" );

		story.remove( 1, 1 );
		story.insert( 2, 1, "e" );
		story.remove( 2, 1 );
		story.insert( "e" );

		story.assertCell( 0, 0, "a" );
		story.assertCell( 0, 1, "b" );
		story.assertCell( 0, 2, "c" );

		story.assertCell( 1, 0, "d" );
		story.assertCell( 1, 1, "f" );

		story.assertCell( 2, 0, "g" );
		story.assertCell( 2, 1, "e" );
		story.assertCell( 2, 2, "h" );
		story.assertCell( 2, 3, "i" );
	}

	private TestGrid copyByReadWrite( TestGrid grid, Collection<Dockable> dockables ){
		Map<Integer, Dockable> idToItem = new HashMap<Integer, Dockable>();
		Map<Dockable, Integer> itemToId = new HashMap<Dockable, Integer>();

		int index = 0;
		for( Dockable item : dockables ) {
			idToItem.put( index, item );
			itemToId.put( item, index );
			index++;
		}

		PlaceholderMap map = grid.toMap( itemToId );
		TestGrid newGrid = new TestGrid();
		newGrid.fromMap( map, idToItem, new PlaceholderToolbarGridConverter<Dockable, ToolbarGridTest.TestItem>(){
			@Override
			public TestItem convert( Dockable dockable, ConvertedPlaceholderListItem item ){
				return new TestItem( (TestDockable) dockable );
			}

			@Override
			public void added( TestItem item ){
				// ignore
			}
		} );
		return newGrid;
	}

	private void assertEqualsGrid( TestGrid gridA, TestGrid gridB ){
		assertEquals( gridA.getColumnCount(), gridB.getColumnCount() );

		GridPlaceholderList<Dockable, DockStation, TestItem> listA = gridA.getGrid();
		GridPlaceholderList<Dockable, DockStation, TestItem> listB = gridB.getGrid();

		assertEquals( listA.list().size(), listB.list().size() );
		assertEquals( listA.dockables().size(), listB.dockables().size() );

		Iterator<GridPlaceholderList<Dockable, DockStation, TestItem>.Item> itemsA = listA.list().iterator();
		Iterator<GridPlaceholderList<Dockable, DockStation, TestItem>.Item> itemsB = listB.list().iterator();

		while( itemsA.hasNext() && itemsB.hasNext() ) {
			assertEqualsColumn( itemsA.next(), itemsB.next() );
		}
		assertEquals( itemsA.hasNext(), itemsB.hasNext() );
	}

	private void assertEqualsColumn( GridPlaceholderList<Dockable, DockStation, TestItem>.Item itemA, GridPlaceholderList<Dockable, DockStation, TestItem>.Item itemB ){
		assertEquals( itemA.isPlaceholder(), itemB.isPlaceholder() );
		assertEquals( itemA.getPlaceholderSet(), itemB.getPlaceholderSet() );

		Column<Dockable, DockStation, TestItem> columnA = itemA.getDockable();
		Column<Dockable, DockStation, TestItem> columnB = itemB.getDockable();

		if( columnA != null && columnB != null ) {
			PlaceholderList<Dockable, DockStation, TestItem> listA = columnA.getList();
			PlaceholderList<Dockable, DockStation, TestItem> listB = columnB.getList();

			assertEqualsList( listA, listB );
		}
	}

	private void assertEqualsList( PlaceholderList<Dockable, DockStation, TestItem> listA, PlaceholderList<Dockable, DockStation, TestItem> listB ){
		assertEquals( listA.list().size(), listB.list().size() );
		assertEquals( listA.dockables().size(), listB.dockables().size() );

		Iterator<PlaceholderList<Dockable, DockStation, TestItem>.Item> itemsA = listA.list().iterator();
		Iterator<PlaceholderList<Dockable, DockStation, TestItem>.Item> itemsB = listB.list().iterator();

		while( itemsA.hasNext() && itemsB.hasNext() ) {
			PlaceholderList<Dockable, DockStation, TestItem>.Item itemA = itemsA.next();
			PlaceholderList<Dockable, DockStation, TestItem>.Item itemB = itemsB.next();

			assertEquals( itemA.getPlaceholderSet(), itemB.getPlaceholderSet() );
			assertEquals( itemA.getDockable(), itemB.getDockable() );
		}

		assertEquals( itemsA.hasNext(), itemsB.hasNext() );
	}

	private class TestStory implements ToolbarColumnModelListener<TestItem>, ToolbarColumnListener<TestItem> {
		private TestGrid grid = new TestGrid();
		private List<Dockable> dockables = new ArrayList<Dockable>();

		private List<ToolbarColumn<TestItem>> columns = new ArrayList<ToolbarColumn<TestItem>>();
		private List<List<TestItem>> items = new ArrayList<List<TestItem>>();

		public TestStory(){
			grid.setStrategy( new TestPlaceholderStrategy() );
			ToolbarColumnModel<TestItem> model = grid.getModel();
			model.addListener( this );
		}

		@Override
		public void inserted( ToolbarColumnModel<TestItem> model, ToolbarColumn<TestItem> column, int index ){
			column.addListener( this );
			columns.add( index, column );
			items.add( index, new ArrayList<TestItem>() );
		}

		@Override
		public void removed( ToolbarColumnModel<TestItem> model, ToolbarColumn<TestItem> column, int index ){
			column.removeListener( this );
			columns.remove( index );
			List<TestItem> list = items.remove( index );
			assertEquals( 0, list.size() );
		}

		@Override
		public void inserted( ToolbarColumn<TestItem> column, TestItem item, Dockable dockable, int index ){
			int columnIndex = columns.indexOf( column );
			assertTrue( columnIndex >= 0 );
			items.get( columnIndex ).add( index, item );
		}

		@Override
		public void removed( ToolbarColumn<TestItem> column, TestItem item, Dockable dockable, int index ){
			int columnIndex = columns.indexOf( column );
			assertTrue( columnIndex >= 0 );
			assertSame( item, items.get( columnIndex ).remove( index ) );
		}

		public void insert( int column, int row, String placeholder ){
			TestItem item = new TestItem( placeholder );
			grid.insert( column, row, item );
			if( placeholder != null ) {
				grid.insertPlaceholder( column, row, new Path( "test", placeholder ) );
			}
			dockables.add( item.asDockable() );
			check();
		}

		public void insert( String placeholder ){
			TestItem item = new TestItem( placeholder );
			grid.put( new Path( "test", placeholder ), item );
		}

		public void remove( int column, int row ){
			ToolbarColumnModel<TestItem> model = grid.getModel();
			TestItem item = model.getColumn( column ).getItem( row );
			grid.remove( item );
			check();
		}

		private void check(){
			assertEqualsGrid( grid, copyByReadWrite( grid, dockables ) );
			assertModel();
		}

		private void assertModel(){
			ToolbarColumnModel<TestItem> model = grid.getModel();
			assertEquals( model.getColumnCount(), items.size() );
			for( int i = 0, n = model.getColumnCount(); i < n; i++ ) {
				ToolbarColumn<TestItem> column = model.getColumn( i );
				List<TestItem> list = items.get( i );

				assertEquals( list.size(), column.getDockableCount() );
				for( int j = 0, m = column.getDockableCount(); j < m; j++ ) {
					assertSame( column.getItem( j ), list.get( j ) );
				}
			}
		}

		public void assertCell( int column, int row, String placeholder ){
			ToolbarColumnModel<TestItem> model = grid.getModel();
			TestItem item = model.getColumn( column ).getItem( row );
			TestDockable dockable = (TestDockable) item.asDockable();
			assertNotNull( dockable );
			if( placeholder == null ) {
				assertNull( dockable.getId() );
			}
			else {
				assertEquals( placeholder, dockable.getId() );
			}
		}
	}

	private static class TestPlaceholderStrategy implements PlaceholderStrategy {
		@Override
		public void install( DockStation station ){
			// ignore
		}

		@Override
		public void uninstall( DockStation station ){
			// ignore
		}

		@Override
		public void addListener( PlaceholderStrategyListener listener ){
			// ignore
		}

		@Override
		public void removeListener( PlaceholderStrategyListener listener ){
			// ignore
		}

		@Override
		public Path getPlaceholderFor( Dockable dockable ){
			if( dockable instanceof TestDockable ) {
				String id = ((TestDockable) dockable).getId();
				if( id != null ) {
					return new Path( "test", id );
				}
			}
			return null;
		}

		@Override
		public boolean isValidPlaceholder( Path placeholder ){
			return true;
		}

	}

	private static class TestDockable extends AbstractDockable {
		private String id;

		public TestDockable( String id ){
			super( PropertyKey.DOCKABLE_TITLE, PropertyKey.DOCKABLE_TOOLTIP );
			this.id = id;
		}

		@Override
		public Component getComponent(){
			return null;
		}

		@Override
		public DockStation asDockStation(){
			return null;
		}

		@Override
		public String getFactoryID(){
			return null;
		}

		@Override
		protected DockIcon createTitleIcon(){
			return null;
		}

		public String getId(){
			return id;
		}
	}

	private static class TestItem implements PlaceholderListItem<Dockable> {
		private Dockable dockable;

		public TestItem( String id ){
			dockable = new TestDockable( id );
		}

		public TestItem( TestDockable dockable ){
			this.dockable = dockable;
		}

		@Override
		public Dockable asDockable(){
			return dockable;
		}

		@Override
		public boolean equals( Object obj ){
			if( obj.getClass() != getClass() ) {
				return false;
			}
			return ((TestItem) obj).dockable == dockable;
		}
	}

	private static class TestGrid extends DockablePlaceholderToolbarGrid<TestItem> {
		private GridPlaceholderList<Dockable, DockStation, TestItem> grid;

		@Override
		protected GridPlaceholderList<Dockable, DockStation, TestItem> createGrid(){
			grid = super.createGrid();
			return grid;
		}

		public GridPlaceholderList<Dockable, DockStation, TestItem> getGrid(){
			return grid;
		}
	}
}
