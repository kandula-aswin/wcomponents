package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.util.TreeItemUtil;
import com.github.bordertech.wcomponents.util.Util;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * WTree represents a tree view selection control.
 *
 * @author Jonathan Austin
 * @since 1.1.0
 */
public class WTree extends AbstractInput
		implements AjaxInternalTrigger, AjaxTarget, AjaxTrigger, SubordinateTrigger, SubordinateTarget,
		Marginable, Targetable {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(WTree.class);

	/**
	 * Tree item request key.
	 */
	private static final String ITEM_REQUEST_KEY = "wc_tiid";

	/**
	 * Scratch map key for the map between an item id and its row index.
	 */
	private static final String INDEX_MAPPING_SCRATCH_MAP_KEY = "itemIdMap";

	/**
	 * Scratch map key for the map between an item id and its custom tree node.
	 */
	private static final String CUSTOM_IDS_SCRATCH_MAP_KEY = "customIdMap";

	/**
	 * Construct the WTree.
	 */
	public WTree() {
		this(Type.VERTICAL);
	}

	/**
	 * @param type the tree type.
	 */
	public WTree(final Type type) {
		setType(type);
	}

	/**
	 * This is used to indicate the type of tree.
	 */
	public enum Type {
		/**
		 * Horizontal tree.
		 */
		HORIZONTAL,
		/**
		 * Vertical tree.
		 */
		VERTICAL
	};

	/**
	 * This is used to control how row selection should work.
	 */
	public enum SelectMode {
		/**
		 * Indicates that only a single row may be selected.
		 */
		SINGLE,
		/**
		 * Indicates that multiple rows may be selected.
		 */
		MULTIPLE
	};

	/**
	 * This is used to control how row expansion should work.
	 */
	public enum ExpandMode {
		/**
		 * Indicates that row expansion occurs on the client.
		 */
		CLIENT,
		/**
		 * Indicates that row expansion occurs once, via AJAX.
		 */
		LAZY,
		/**
		 * Indicates that row expansion should make an AJAX call every time.
		 */
		DYNAMIC
	};

	/**
	 * Returns a {@link Set} of the selected options. If no options have been selected, then it returns an empty list.
	 * <p>
	 * As getValue calls {@link #getData()} for the currently selected options, it usually expects getData to return
	 * null (for no selection) or a {@link List} of selected options. If the data returned by getData is not null and is
	 * not a List, then setData will either (1) if the data is an array, convert the array to a List or (2) create a
	 * List and add the data as the selected option.
	 * </p>
	 * <p>
	 * getValue will verify the selected option/s are valid. If a selected option does not exist, then it will throw an
	 * {@link IllegalArgumentException}.
	 * </p>
	 *
	 * @return the selected options in the given UI context.
	 */
	@Override
	public Set<String> getValue() {
		// Convert data to a set (if necessary)
		Set<String> data = TreeItemUtil.convertDataToSet(getData());
		if (data == null || data.isEmpty()) {
			return Collections.EMPTY_SET;
		}
		// TODO Similar to select lists, consider validating the selected items
		return data;
	}

	/**
	 * Returns a string value of the selected item for this users session. If multiple selections have been made, this
	 * will be a comma separated list of string values. If no value is selected, null is returned.
	 *
	 * @return the selected item value as a rendered String
	 */
	@Override
	public String getValueAsString() {
		Set<String> selected = getValue();
		if (selected == null || selected.isEmpty()) {
			return null;
		}

		StringBuilder stringValues = new StringBuilder();
		boolean first = true;

		for (String item : selected) {
			if (!first) {
				stringValues.append(", ");
			}
			stringValues.append(item);
			first = false;
		}

		return stringValues.toString();
	}

	/**
	 * @return true if nothing selected
	 */
	@Override
	public boolean isEmpty() {
		Set<String> selected = getValue();
		return (selected == null || selected.isEmpty());
	}

	/**
	 *
	 * @return the tree type.
	 */
	public Type getType() {
		return getComponentModel().type;
	}

	/**
	 * @param type the tree type
	 */
	public void setType(final Type type) {
		getOrCreateComponentModel().type = type == null ? Type.VERTICAL : type;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Margin getMargin() {
		return getComponentModel().margin;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMargin(final Margin margin) {
		getOrCreateComponentModel().margin = margin;
	}

	/**
	 * @return the tree model
	 */
	public TreeItemModel getTreeModel() {
		return getComponentModel().treeModel;
	}

	/**
	 * Sets the tree model which provides row data.
	 *
	 * @param treeModel the tree model.
	 */
	public void setTreeModel(final TreeItemModel treeModel) {
		getOrCreateComponentModel().treeModel = treeModel;
		clearItemIdIndexMap();
		setSelectedRows(null);
		setExpandedRows(null);
		setCustomTree((TreeItemIdNode) null);
	}

	/**
	 * The action when a request to open a tree item is received.
	 *
	 * @return the open action
	 */
	public Action getOpenAction() {
		return getComponentModel().openAction;
	}

	/**
	 * The action when a request to open a tree item is received.
	 *
	 * @param action the open action
	 */
	public void setOpenAction(final Action action) {
		getOrCreateComponentModel().openAction = action;
	}

	/**
	 * @return true if allow the client to shuffle items
	 */
	public boolean isShuffle() {
		return getComponentModel().shuffle;
	}

	/**
	 *
	 * @param shuffle true if allow the client to shuffle items
	 */
	public void setShuffle(final boolean shuffle) {
		getOrCreateComponentModel().shuffle = shuffle;
	}

	/**
	 * The action used when the tree items are shuffled on the client.
	 *
	 * @return the shuffle action
	 */
	public Action getShuffleAction() {
		return getComponentModel().shuffleAction;
	}

	/**
	 * The action used when the tree items are shuffled on the client.
	 *
	 * @param action the shuffle action
	 */
	public void setShuffleAction(final Action action) {
		getOrCreateComponentModel().shuffleAction = action;
	}

	/**
	 * @return the root node of a custom tree structure
	 */
	public TreeItemIdNode getCustomTree() {
		return getComponentModel().customTree;
	}

	/**
	 * @param json the json representing a custom tree structure
	 */
	public void setCustomTree(final String json) {
		TreeItemIdNode root = TreeItemUtil.convertJsonToTree(json);
		setCustomTree(root);
	}

	/**
	 * @param customTree the root node of a custom tree structure
	 */
	public void setCustomTree(final TreeItemIdNode customTree) {
		getOrCreateComponentModel().customTree = customTree;
		clearCustomIdMap();
	}

	/**
	 * @return the row selection mode.
	 */
	public SelectMode getSelectMode() {
		return getComponentModel().selectMode;
	}

	/**
	 * Sets the row selection mode.
	 *
	 * @param selectMode the row selection mode to set.
	 */
	public void setSelectMode(final SelectMode selectMode) {
		getOrCreateComponentModel().selectMode = selectMode == null ? SelectMode.SINGLE : selectMode;
	}

	/**
	 * @return the row expansion mode.
	 */
	public ExpandMode getExpandMode() {
		return getComponentModel().expandMode;
	}

	/**
	 * Sets the row expansion mode.
	 *
	 * @param expandMode the expand mode to set.
	 */
	public void setExpandMode(final ExpandMode expandMode) {
		getOrCreateComponentModel().expandMode = expandMode == null ? ExpandMode.CLIENT : expandMode;
	}

	/**
	 * Retrieve the row keys that are selected.
	 * <p>
	 * A row key uniquely identifies each row and is determined by the {@link TreeItemModel}. Refer to
	 * {@link TreeItemModel#getItemId(List)}.
	 * </p>
	 *
	 * @return the selected row keys.
	 */
	public Set<String> getSelectedRows() {
		return getValue();
	}

	/**
	 * Set the row keys that are selected.
	 * <p>
	 * A row key uniquely identifies each row and is determined by the {@link TreeItemModel}. Refer to
	 * {@link TreeItemModel#getItemId(List)}.
	 * </p>
	 *
	 * @param itemIds the keys of selected rows.
	 */
	public void setSelectedRows(final Set<String> itemIds) {
		setData(itemIds);
	}

	/**
	 * Retrieve the row keys that are expanded.
	 * <p>
	 * A row key uniquely identifies each row and is determined by the {@link TreeItemModel}. Refer to
	 * {@link TreeItemModel#getItemId(List)}.
	 * </p>
	 *
	 * @return the expanded row keys.
	 */
	public Set<String> getExpandedRows() {
		Set<String> keys = getComponentModel().expandedRows;
		if (keys == null) {
			return Collections.emptySet();
		} else {
			return Collections.unmodifiableSet(keys);
		}
	}

	/**
	 * Set the row keys that are expanded.
	 * <p>
	 * A row key uniquely identifies each row and is determined by the {@link TreeItemModel}. Refer to
	 * {@link TreeItemModel#getItemId(List)}.
	 * </p>
	 *
	 * @param itemIds the keys of expanded rows.
	 */
	public void setExpandedRows(final Set<String> itemIds) {
		getOrCreateComponentModel().expandedRows = itemIds;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTargetId() {
		return getId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<String> getRequestValue(final Request request) {
		if (isPresent(request)) {
			return getNewSelections(request);
		} else {
			return getValue();
		}
	}

	/**
	 * Retrieves a URL for the tree item image.
	 * <p>
	 * This method is used by the WTree Renderer.
	 * </p>
	 *
	 * @param item the tree item
	 * @param itemId the tree item id
	 * @return the URL to access the tree item image.
	 */
	public String getItemImageUrl(final TreeItemImage item, final String itemId) {

		if (item == null) {
			return null;
		}

		// Check if has image url
		String url = item.getUrl();
		if (!Util.empty(url)) {
			return url;
		}

		// Check if has image
		Image image = item.getImage();
		if (image == null) {
			return null;
		}

		// Check static resource
		if (image instanceof InternalResource) {
			return ((InternalResource) image).getTargetUrl();
		}

		// Build targetted url
		Environment env = getEnvironment();
		Map<String, String> parameters = env.getHiddenParameters();
		parameters.put(Environment.TARGET_ID, getTargetId());

		String cacheKey = item.getImageCacheKey();

		if (Util.empty(cacheKey)) {
			// Add some randomness to the URL to prevent caching
			String random = WebUtilities.generateRandom();
			parameters.put(Environment.UNIQUE_RANDOM_PARAM, random);
		} else {
			// Remove step counter as not required for cached content
			parameters.remove(Environment.STEP_VARIABLE);
			parameters.remove(Environment.SESSION_TOKEN_VARIABLE);
			// Add the cache key
			parameters.put(Environment.CONTENT_CACHE_KEY, cacheKey);
		}

		// Item id
		parameters.put(ITEM_REQUEST_KEY, itemId);

		// The targetable path needs to be configured for the portal environment.
		url = env.getWServletPath();

		// Note the last parameter. In javascript we don't want to encode "&".
		return WebUtilities.getPath(url, parameters, true);
	}

	/**
	 *
	 * @return the prefix to use on the tree item ids
	 */
	public String getItemIdPrefix() {
		return getId() + "-";
	}

	/**
	 * Clear the map holding the mapping between custom item ids and their node item.
	 */
	public void clearCustomIdMap() {
		if (getScratchMap() != null) {
			getScratchMap().remove(CUSTOM_IDS_SCRATCH_MAP_KEY);
		}
	}

	/**
	 * @return the map between the custom item ids and their node item.
	 */
	public Map<String, TreeItemIdNode> getCustomIdMap() {
		if (getScratchMap() == null) {
			// No user context present
			return TreeItemUtil.createCustomIdMap(getCustomTree());
		}
		Map<String, TreeItemIdNode> map = (Map<String, TreeItemIdNode>) getScratchMap().get(CUSTOM_IDS_SCRATCH_MAP_KEY);
		if (map == null) {
			map = TreeItemUtil.createCustomIdMap(getCustomTree());
			getScratchMap().put(CUSTOM_IDS_SCRATCH_MAP_KEY, map);
		}
		return map;
	}

	/**
	 * Clear the map holding the mapping between an item id and its row index.
	 */
	public void clearItemIdIndexMap() {
		if (getScratchMap() != null) {
			getScratchMap().remove(INDEX_MAPPING_SCRATCH_MAP_KEY);
		}
	}

	/**
	 * @return the mapping between an item id and its row index. Only expanded items.
	 */
	public Map<String, List<Integer>> getItemIdIndexMap() {
		if (getScratchMap() == null) {
			// No user context present
			return TreeItemUtil.createItemIdIndexMap(this);
		}
		Map<String, List<Integer>> map = (Map<String, List<Integer>>) getScratchMap().get(INDEX_MAPPING_SCRATCH_MAP_KEY);
		if (map == null) {
			map = TreeItemUtil.createItemIdIndexMap(this);
			getScratchMap().put(INDEX_MAPPING_SCRATCH_MAP_KEY, map);
		}
		return map;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialiseComponentModel() {
		super.initialiseComponentModel();
		// Copy the custom tree (if set) to allow the nodes to be updated per user
		TreeItemIdNode custom = getCustomTree();
		if (custom != null) {
			TreeItemIdNode copy = TreeItemUtil.copyTreeNode(custom);
			setCustomTree(copy);
		}
	}

	/**
	 * Override preparePaint to register an AJAX operation.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		super.preparePaintComponent(request);

		// If is an internal AJAX action, set the action type.
		if (AjaxHelper.isCurrentAjaxTrigger(this)) {
			AjaxOperation operation = AjaxHelper.getCurrentOperation();
			if (operation.isInternalAjaxRequest()) {
				operation.setAction(AjaxOperation.AjaxAction.IN);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void afterPaint(final RenderContext renderContext) {
		super.afterPaint(renderContext);
		// Clear the open id (if set)
		setOpenRequestItemId(null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean beforeHandleRequest(final Request request) {

		// Clear open request (if set)
		setOpenRequestItemId(null);

		// Check if is targeted request (ie item image)
		String targetParam = request.getParameter(Environment.TARGET_ID);
		boolean targetted = (targetParam != null && targetParam.equals(getTargetId()));
		if (targetted) {
			handleItemImageRequest(request);
			return false;
		}

		// Check if open item request
		if (isOpenItemRequest(request)) {
			handleOpenItemRequest(request);
			return false;
		}
		return true;
	}

	/**
	 * @param request the request being processed
	 * @return true if its an open item request
	 */
	protected boolean isOpenItemRequest(final Request request) {
		return AjaxHelper.isCurrentAjaxTrigger(this) && request.getParameter(ITEM_REQUEST_KEY) != null;
	}

	/**
	 * Set the inputs based on the incoming request. The text input values are set as an array of strings on the
	 * parameter with this name {@link #getName()}. Any empty strings will be ignored.
	 *
	 * @param request the current request.
	 * @return true if the inputs have changed, otherwise return false
	 */
	@Override
	protected boolean doHandleRequest(final Request request) {

		Set<String> values = getRequestValue(request);
		Set<String> current = getValue();

		boolean changed = !selectionsEqual(values, current);
		if (changed) {
			setData(values);
		}

		if (isShuffle()) {
			handleShuffleState(request);
		}
		handleExpandedState(request);

		return changed;
	}

	/**
	 * <p>
	 * Indicates whether this tree was present in the request.
	 * </p>
	 *
	 * @param request the request being responded to.
	 * @return true if this tree was present in the request, false if not.
	 */
	@Override
	protected boolean isPresent(final Request request) {
		return request.getParameter(getId() + "-h") != null;
	}

	/**
	 * Handles a request containing row selection data.
	 *
	 * @param request the request containing row selection data.
	 * @return the set of selected item ids.
	 */
	private Set<String> getNewSelections(final Request request) {

		// Check for any selections on the request
		String[] paramValue = request.getParameterValues(getId());
		String[] selectedRowIds = removeEmptyStrings(paramValue);
		if (selectedRowIds == null || selectedRowIds.length == 0) {
			return Collections.EMPTY_SET;
		}

		Set<String> newSelectionIds = new HashSet<>();

		boolean singleSelect = getSelectMode() == SelectMode.SINGLE;

		int offset = getItemIdPrefix().length();
		for (String selectedRowId : selectedRowIds) {
			if (selectedRowId.length() <= offset) {
				LOG.warn("Selected row id [" + selectedRowId + "] does not have a valid prefix and will be ignored.");
				continue;
			}
			String itemId = selectedRowId.substring(offset);
			if (isValidTreeItem(itemId)) {
				newSelectionIds.add(itemId);
				if (singleSelect) {
					break;
				}
			} else {
				LOG.warn("Selected row id [" + itemId + "] is not valid and will be ignored.");
			}
		}

		return newSelectionIds;
	}

	/**
	 * Selection lists are considered equal if they have the same items (order is not important). An empty list is
	 * considered equal to a null list.
	 *
	 * @param set1 the first list to check.
	 * @param set2 the second list to check.
	 * @return true if the lists are equal, false otherwise.
	 */
	private boolean selectionsEqual(final Set<?> set1, final Set<?> set2) {
		// Empty or null lists
		if ((set1 == null || set1.isEmpty()) && (set2 == null || set2.isEmpty())) {
			return true;
		}

		// Same size and contain same entries
		return set1 != null && set2 != null && set1.size() == set2.size() && set1.
				containsAll(set2);
	}

	/**
	 * Handle a targeted request to retrieve the tree item image.
	 *
	 * @param request the request being processed
	 */
	private void handleItemImageRequest(final Request request) {

		// Check for tree item id
		String itemId = request.getParameter(ITEM_REQUEST_KEY);
		if (itemId == null) {
			throw new SystemException("No tree item id provided for image request.");
		}

		// Check valid item id
		if (!isValidTreeItem(itemId)) {
			throw new SystemException("Tree item id for an image request [" + itemId + "] is not valid.");
		}

		List<Integer> index = getItemIdIndexMap().get(itemId);
		TreeItemImage image = getTreeModel().getItemImage(index);
		if (image == null) {
			throw new SystemException("Tree item id [" + itemId + "] does not have an image.");
		}

		ContentEscape escape = new ContentEscape(image.getImage());
		throw escape;
	}

	/**
	 * Handles a request containing an open request.
	 *
	 * @param request the request containing row open request.
	 */
	private void handleOpenItemRequest(final Request request) {

		// Check for tree item id
		String param = request.getParameter(ITEM_REQUEST_KEY);
		if (param == null) {
			throw new SystemException("No tree item id provided for open request.");
		}

		// Get the item id
		int offset = getItemIdPrefix().length();
		if (param.length() <= offset) {
			throw new SystemException("Tree item id [" + param + "] does not have the correct prefix value.");
		}
		String itemId = param.substring(offset);

		// Check is a valid item id
		if (!isValidTreeItem(itemId)) {
			throw new SystemException("Tree item id [" + itemId + "] is not valid.");
		}

		// Check valid and expandable
		TreeItemIdNode custom = getCustomTree();
		if (custom == null) {
			List<Integer> rowIndex = getItemIdIndexMap().get(itemId);
			if (!getTreeModel().isExpandable(rowIndex)) {
				throw new SystemException("Tree item id [" + itemId + "] is not expandable.");
			}
		} else {
			TreeItemIdNode node = getCustomIdMap().get(itemId);
			if (!node.hasChildren()) {
				throw new SystemException("Tree item id [" + itemId + "] is not expandable in custom tree.");
			}
			// Add children from the model (if needed)
			if (node.getChildren().isEmpty()) {
				TreeItemUtil.loadCustomNodeChildren(node, this);
			}
		}

		// Save the open id
		setOpenRequestItemId(itemId);

		// Set the expanded rows
		switch (getType()) {
			case VERTICAL:
				// For vertical tree, just add the open item to the expanded rows
				Set<String> rows = new HashSet(getExpandedRows());
				rows.add(itemId);
				setExpandedRows(rows);
				break;
			case HORIZONTAL:
				// Find expanded rows to reach the open item id (ie only keep the current branch expanded)
				Set<String> expanded;
				if (custom == null) {
					List<Integer> rowIndex = getItemIdIndexMap().get(itemId);
					expanded = TreeItemUtil.calcExpandedRowsToReachIndex(rowIndex, getTreeModel());
				} else {
					expanded = TreeItemUtil.calcCustomExpandedRowsToReachItemId(itemId, this);
				}
				setExpandedRows(expanded);
				break;
		}

		// Run the open action (if set)
		final Action action = getOpenAction();
		if (action != null) {
			final ActionEvent event = new ActionEvent(this, "openItem");
			Runnable later = new Runnable() {
				@Override
				public void run() {
					action.execute(event);
				}
			};
			invokeLater(later);
		}

	}

	/**
	 * Handle the tree items that have been shuffled by the client.
	 *
	 * @param request the request being processed
	 */
	private void handleShuffleState(final Request request) {

		String json = request.getParameter(getId() + ".shuffle");
		if (Util.empty(json)) {
			return;
		}

		// New
		TreeItemIdNode newTree;
		try {
			newTree = TreeItemUtil.convertJsonToTree(json);
		} catch (Exception e) {
			LOG.warn("Could not parse JSON for shuffle tree items. " + e.getMessage());
			return;
		}

		// Current
		TreeItemIdNode currentTree = getCustomTree();

		boolean changed = !TreeItemUtil.isTreeSame(newTree, currentTree);

		if (changed) {
			setCustomTree(newTree);
			// Run the shuffle action (if set)
			final Action action = getShuffleAction();
			if (action != null) {
				final ActionEvent event = new ActionEvent(this, "shuffle");
				Runnable later = new Runnable() {
					@Override
					public void run() {
						action.execute(event);
					}
				};
				invokeLater(later);
			}
		}
	}

	/**
	 * Handle the current expanded state.
	 *
	 * @param request the request containing row expansion data.
	 */
	private void handleExpandedState(final Request request) {

		String[] paramValue = request.getParameterValues(getId() + ".open");
		if (paramValue == null) {
			paramValue = new String[0];
		}

		String[] expandedRowIds = removeEmptyStrings(paramValue);
		Set<String> newExpansionIds = new HashSet<>();

		if (expandedRowIds != null) {
			int offset = getItemIdPrefix().length();
			for (String expandedRowId : expandedRowIds) {
				if (expandedRowId.length() <= offset) {
					LOG.warn("Expanded row id [" + expandedRowId + "] does not have a valid prefix and will be ignored.");
					continue;
				}
				String itemId = expandedRowId.substring(offset);
				if (isValidTreeItem(itemId)) {
					newExpansionIds.add(itemId);
				} else {
					LOG.warn("Expanded row id [" + itemId + "] is not valid and will be ignored.");
				}
			}
		}
		setExpandedRows(newExpansionIds);
	}

	/**
	 * Helper that removes empty/null string from the <code>original</code> string array.
	 *
	 * @param originals The string array from which the null/empty strings should be removed from.
	 * @return Array of non empty strings from the <code>original</code> string array.
	 */
	private String[] removeEmptyStrings(final String[] originals) {
		if (originals == null) {
			return null;
		} else {
			List<String> parsed = new ArrayList<>();

			for (String original : originals) {
				if (original != null && original.length() > 0) {
					parsed.add(original);
				}
			}

			return parsed.toArray(new String[parsed.size()]);
		}
	}

	/**
	 *
	 * @param itemId the item id to check
	 * @return true if this item id is valid.
	 */
	private boolean isValidTreeItem(final String itemId) {

		// Check for custom tree
		TreeItemIdNode custom = getCustomTree();
		if (custom != null) {
			return getCustomIdMap().containsKey(itemId);
		}

		// Check is still a valid item
		return getItemIdIndexMap().containsKey(itemId);
	}

	/**
	 * @param itemId the item id to open
	 */
	private void setOpenRequestItemId(final String itemId) {
		getOrCreateComponentModel().openRequestItemId = itemId;
	}

	/**
	 * @return the item id to open, or null
	 */
	public String getOpenRequestItemId() {
		return getComponentModel().openRequestItemId;
	}

	/**
	 * @return a String representation of this component, for debugging purposes.
	 */
	@Override
	public String toString() {
		TreeItemModel model = getTreeModel();
		return toString(model.getClass().getSimpleName(), -1, -1);
	}

	/**
	 * Creates a new component model.
	 *
	 * @return a new WTreeComponentModel.
	 */
	@Override
	protected WTreeComponentModel newComponentModel() {
		return new WTreeComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// for type safety only
	protected WTreeComponentModel getComponentModel() {
		return (WTreeComponentModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// for type safety only
	protected WTreeComponentModel getOrCreateComponentModel() {
		return (WTreeComponentModel) super.getOrCreateComponentModel();

	}

	/**
	 * Contains the tree's UI state.
	 *
	 * @author Jonathan Austin
	 * @since 1.1.0
	 */
	public static final class WTreeComponentModel extends InputModel {

		/**
		 * The type of tree.
		 */
		private Type type;

		/**
		 * The margins to be used on the tree.
		 */
		private Margin margin;

		/**
		 * The data model for the tree.
		 */
		private TreeItemModel treeModel = EmptyTreeItemModel.INSTANCE;

		// Selection
		/**
		 * Indicates how row selection should function.
		 */
		private SelectMode selectMode = SelectMode.SINGLE;

		/**
		 * Indicates how row expansion should function.
		 */
		private ExpandMode expandMode = ExpandMode.CLIENT;

		/**
		 * Holds the keys of currently expanded rows.
		 */
		private Set<String> expandedRows;

		/**
		 * Allow the rows to be shuffled.
		 */
		private boolean shuffle;

		/**
		 * Shuffle action.
		 */
		private Action shuffleAction;

		/**
		 * Open action.
		 */
		private Action openAction;

		/**
		 * Open item id.
		 */
		private String openRequestItemId;

		/**
		 * This is used to allow a user to have a different tree of nodes.
		 */
		private TreeItemIdNode customTree;
	}

}
