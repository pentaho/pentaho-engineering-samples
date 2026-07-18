/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2016 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/

define(function() {

  "use strict";

  return {
    rules: [
      // Sample rule
      {
        priority: -1,
        select: {
          module: "./Model"
        },
        apply: {
          props: {
            barSize: {defaultValue: 50}
          }
        }
      },

      // Analyzer integration
      {
        priority: -1,
        select: {
          module: "./Model",
          annotation: "pentaho/analyzer/visual/Options",
          application: "pentaho/analyzer"
        },
        apply: {
          keepLevelOnDrilldown: false
        }
      }
    ]
  };
});
