define(["wc/i18n/i18n", "wc/dom/event", "wc/dom/focus", "wc/dom/initialise", "wc/dom/getViewportSize", "wc/dom/shed", "wc/dom/Widget", "wc/has", "wc/config"],
	function(i18n, event, focus, initialise, getViewportSize, shed, Widget, has, wcconfig) {
		"use strict";

		if (has("ie")) {
			return null;
		}

		/**
		 * @constructor
		 * @alias module:wc/ui/backToTop~BackToTop
		 * @private
		 */
		function BackToTop() {
			var
				/**
				 * The description of the back to top link HTML artifact.
				 * @var
				 * @type {module:wc/dom/Widget}
				 * @private
				 */
				backWidget = new Widget("a", "wc_btt"),
				/**
				 * This property can be set to a positive integer to force showing the scroll to top link at X pixels of
				 * scroll. If it is not set (or set to 0) then the scroll to top link will appear when more than one
				 * viewport height of scroll has occurred.
				 *
				 * Can be set in module configuration as property "scroll".
				 *
				 * @constant
				 * @type {int}
				 * @private
				 * @default 0
				 */
				MIN_SCROLL_BEFORE_SHOW = 0,
				/**
				 * Is the back to top link enabled?
				 * @var
				 * @type Boolean
				 * @private
				 */
				isEnabled = true,
				/**
				 * Custon configuration
				 * @type Object
				 */
				config;

			/**
			 * Click event handler to scroll the page when the back to top link is clicked.
			 *
			 * @function
			 * @private
			 * @param {Event} $event The wrapped click event.
			 */
			function clickEvent($event) {
				var docEl = document.documentElement;

				if (!isEnabled || $event.defaultPrevented) {
					return;
				}
				if (backWidget.findAncestor($event.target)) {
					$event.preventDefault();
					if (docEl.scrollIntoView) {
						docEl.scrollIntoView();
					} else {
						docEl.scrollTop = 0;
					}
					focus.focusFirstTabstop(docEl);  // this would actually be sufficient if we could guarantee a focusable element.
				}
			}

			/**
			 * Toggles the visibility of the back to top link based on the argument show.
			 *
			 * @function
			 * @private
			 * @param {boolean} [show] If true the back to top link is shown, otherwise it is hidden.
			 */
			function toggle(show) {
				var link = backWidget.findDescendant(document.body);
				if (show) {
					if (!link) {
						link = document.createElement("a");
						link.className = "wc_btt";
						link.href = "#";
						link.innerHTML = "<i class='fa fa-chevron-circle-up fa-5x'></i><span class='wc-off'>" + i18n.get("back_to_top") + "</span>";
						document.body.appendChild(link);
					}
					shed.show(link, true);  // nothing needs to be notified that the back to top link is showing
				} else if (link) {
					shed.hide(link, true);  // nothing needs to be notified that the back to top link is hidden
				}
			}

			/**
			 * Hide the back to top link when the ESCAPE key is pressed.
			 *
			 * @function
			 * @private
			 * @param {Event} $event The wrapped keydown event.
			 */
			function keyEvent($event) {
				if ($event.keyCode === window.KeyEvent.DOM_VK_ESCAPE) {
					toggle();
				}
			}

			/**
			 * Event listener to show or hide the back to top link after scroll or resize events.
			 *
			 * @function
			 * @private
			 */
			function genericEvent() {
				var scroll = document.documentElement.scrollTop || document.body.scrollTop,
					min;

				if (MIN_SCROLL_BEFORE_SHOW > 0) {
					min = MIN_SCROLL_BEFORE_SHOW;
				} else {
					min = getViewportSize().height;
				}
				toggle((scroll > min));
			}

			/**
			 * Helper function to add or remove event handlers when the back to top link is disabled or enabled.
			 * @param {Boolean} enable Indicates if the BTT link is enabled (true) or disabled.
			 */
			function addRemoveEventHandlers(enable) {
				var func = enable ? "add" : "remove",
					el = document.body;
				event[func](el, event.TYPE.click, clickEvent);
				event[func](el, event.TYPE.keydown, keyEvent);
				event[func](window, event.TYPE.scroll, genericEvent);
				event[func](window, event.TYPE.resize, genericEvent);
			}

			/**
			 * Set up function to wire up event listeners.
			 *
			 * @function module:wc/ui/backToTop.initialise
			 * @public
			 */
			this.initialise = function(/* element */) {
				this.setEnabled(true);
			};

			/**
			 * Allow an external script to turn off (or on) the back to top link if required.
			 * @function module:wc/ui/backToTop.setEnabled
			 * @param {Boolean} enable Set true to enable the back to top link (default) or false to disable.
			 */
			this.setEnabled = function(enable) {
				isEnabled = !!enable;
				if (enable) {
					config = config || wcconfig.get("wc/ui/backToTop", {
						scroll: MIN_SCROLL_BEFORE_SHOW
					});
					MIN_SCROLL_BEFORE_SHOW = config.scroll;
				} else {
					toggle(false); // just in case the link is showing at the time it is turned off.
				}
				addRemoveEventHandlers(enable);
			};
		}

		/**
		 * Provides functionality to provide a back to top link which is scroll and viewport size aware.
		 *
		 * @module
		 * @requires module:wc/i18n/i18n
		 * @requires module:wc/dom/event
		 * @requires module:wc/dom/focus
		 * @requires module:wc/dom/initialise
		 * @requires module:wc/dom/getViewportSize
		 * @requires module:wc/dom/shed
		 * @requires module:wc/dom/Widget
		 * @requires module:wc/has
		 * @requires module:wc/config
		 */
		return initialise.register(new BackToTop());

		/**
		 * @typedef {Object} module:wc/ui/backToTop~config Configuration for the back to top link.
		 * @property {int} scroll The number of pixels to scroll before showing the back to top link. If 0 then the scroll to top link will appear
		 *  when more than one viewport height of scroll has occurred.
		 */
	});
