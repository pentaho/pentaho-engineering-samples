/*!
 * Copyright 2010 - 2017 Pentaho Corporation. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
define([
  "module",
  "pentaho/visual/base"
], function(module, baseModelFactory) {

  "use strict";

  // Return the Model factory function.
  return function(context) {

    // Obtain the base Model class from the context, given the base Model's factory function.
    var BaseModel = context.get(baseModelFactory);

    // Create the Bar Model subclass
    var BarModel = BaseModel.extend({
      $type: {
        id: module.id,

        // CSS class.
        styleClass: "pentaho-visual-samples-bar",

        // The label may show up in menus.
        label: "D3 Bar Chart",

        // The default view to use to render this visualization is
        // a sibling module named `view-d3.js`.
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
            levels: "ordinal",
            attributes: {isRequired: true, countMax: 1}
          },
          {
            name: "measure",
            base: "pentaho/visual/role/property",
            levels: "quantitative",
            dataType: "number",
            attributes: {isRequired: true, countMax: 1}
          }
        ]
      }
    });

    return BarModel;
  };
});
