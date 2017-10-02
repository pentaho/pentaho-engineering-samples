/* The contents of this file are based on
 * from http://bl.ocks.org/ropeladder/83915942ac42f17c087a82001418f2ee
 */
define(["d3"], function(d3) {

  function clickController() {
    // we want to a distinguish single/double click
    // details http://bl.ocks.org/couchand/6394506
    var dispatcher = d3.dispatch('click', 'dblclick');

    function cc(selection) {
      var down,
          tolerance = 5,
          last,
          wait = null,
          args;

      // euclidean distance
      function dist(a, b) {
        return Math.sqrt(Math.pow(a[0] - b[0], 2), Math.pow(a[1] - b[1], 2));
      }

      selection.on('mousedown', function() {
        down = d3.mouse(document.body);
        last = +new Date();
        args = arguments;
      });

      selection.on('mouseup', function() {
        if (dist(down, d3.mouse(document.body)) > tolerance) {
          return;
        }

        if (wait) {
          window.clearTimeout(wait);
          wait = null;
          dispatcher.apply("dblclick", this, args);
        } else {
          wait = window.setTimeout((function() {
            return function() {
              dispatcher.apply("click", this, args);
              wait = null;
            };
          })(), 300);
        }
      });
    };

    return d3rebind(cc, dispatcher, 'on');
  }

  return clickController;

  // Copies a variable number of methods from source to target.
  function d3rebind(target, source) {
    var i = 1, n = arguments.length, method;
    while (++i < n) target[method = arguments[i]] = d3_rebind(target, source, source[method]);
    return target;
  }

  // Method is assumed to be a standard D3 getter-setter:
  // If passed with no arguments, gets the value.
  // If passed with arguments, sets the value and returns the target.
  function d3_rebind(target, source, method) {
    return function() {
      var value = method.apply(source, arguments);
      return value === source ? target : value;
    };
  }
});
