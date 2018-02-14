/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright 2016 - 2017 Hitachi Vantara. All rights reserved.
 */
define([
  "module",
  "css!./css/model"
], function(module) {
  "use strict";

  return ["pentaho/visual/base/model", function(BaseModel) {
    // Create the Bar Model subclass
    var BarModel = BaseModel.extend({
      $type: {
        id: module.id,

        // CSS class
        styleClass: "pentaho-visual-samples-bar-d3",

        // The label may show up in menus
        label: "D3 Bar Chart",

        // The default view to use to render this visualization is
        // a sibling module named `view-d3.js`
        defaultView: "./view-d3",

        // Properties
        props: [
          // General properties
          {
            name: "barSize",
            valueType: "number",
            defaultValue: 30,
            isRequired: true
          },

          // Visual role properties
          {
            name: "category",
            base: "pentaho/visual/role/property",
            attributes: {isRequired: true}
          },
          {
            name: "measure",
            base: "pentaho/visual/role/property",
            modes: [{dataType: "number"}],
            attributes: {isRequired: true, countMax: 1}
          },

          // Palette property
          {
            name: "palette",
            base: "pentaho/visual/color/paletteProperty",
            levels: "nominal",
            isRequired: true
          }
        ]
      }
    });

    return BarModel;
  }];
});
