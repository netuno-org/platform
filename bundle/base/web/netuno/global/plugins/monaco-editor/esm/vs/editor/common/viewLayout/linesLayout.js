/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
import { WhitespaceComputer } from './whitespaceComputer.js';
/**
 * Layouting of objects that take vertical space (by having a height) and push down other objects.
 *
 * These objects are basically either text (lines) or spaces between those lines (whitespaces).
 * This provides commodity operations for working with lines that contain whitespace that pushes lines lower (vertically).
 * This is written with no knowledge of an editor in mind.
 */
var LinesLayout = /** @class */ (function () {
    function LinesLayout(lineCount, lineHeight) {
        this._lineCount = lineCount;
        this._lineHeight = lineHeight;
        this._whitespaces = new WhitespaceComputer();
    }
    /**
     * Change the height of a line in pixels.
     */
    LinesLayout.prototype.setLineHeight = function (lineHeight) {
        this._lineHeight = lineHeight;
    };
    /**
     * Set the number of lines.
     *
     * @param lineCount New number of lines.
     */
    LinesLayout.prototype.onFlushed = function (lineCount) {
        this._lineCount = lineCount;
    };
    /**
     * Insert a new whitespace of a certain height after a line number.
     * The whitespace has a "sticky" characteristic.
     * Irrespective of edits above or below `afterLineNumber`, the whitespace will follow the initial line.
     *
     * @param afterLineNumber The conceptual position of this whitespace. The whitespace will follow this line as best as possible even when deleting/inserting lines above/below.
     * @param heightInPx The height of the whitespace, in pixels.
     * @return An id that can be used later to mutate or delete the whitespace
     */
    LinesLayout.prototype.insertWhitespace = function (afterLineNumber, ordinal, heightInPx, minWidth) {
        return this._whitespaces.insertWhitespace(afterLineNumber, ordinal, heightInPx, minWidth);
    };
    /**
     * Change properties associated with a certain whitespace.
     */
    LinesLayout.prototype.changeWhitespace = function (id, newAfterLineNumber, newHeight) {
        return this._whitespaces.changeWhitespace(id, newAfterLineNumber, newHeight);
    };
    /**
     * Remove an existing whitespace.
     *
     * @param id The whitespace to remove
     * @return Returns true if the whitespace is found and it is removed.
     */
    LinesLayout.prototype.removeWhitespace = function (id) {
        return this._whitespaces.removeWhitespace(id);
    };
    /**
     * Notify the layouter that lines have been deleted (a continuous zone of lines).
     *
     * @param fromLineNumber The line number at which the deletion started, inclusive
     * @param toLineNumber The line number at which the deletion ended, inclusive
     */
    LinesLayout.prototype.onLinesDeleted = function (fromLineNumber, toLineNumber) {
        this._lineCount -= (toLineNumber - fromLineNumber + 1);
        this._whitespaces.onLinesDeleted(fromLineNumber, toLineNumber);
    };
    /**
     * Notify the layouter that lines have been inserted (a continuous zone of lines).
     *
     * @param fromLineNumber The line number at which the insertion started, inclusive
     * @param toLineNumber The line number at which the insertion ended, inclusive.
     */
    LinesLayout.prototype.onLinesInserted = function (fromLineNumber, toLineNumber) {
        this._lineCount += (toLineNumber - fromLineNumber + 1);
        this._whitespaces.onLinesInserted(fromLineNumber, toLineNumber);
    };
    /**
     * Get the sum of heights for all objects.
     *
     * @return The sum of heights for all objects.
     */
    LinesLayout.prototype.getLinesTotalHeight = function () {
        var linesHeight = this._lineHeight * this._lineCount;
        var whitespacesHeight = this._whitespaces.getTotalHeight();
        return linesHeight + whitespacesHeight;
    };
    /**
     * Get the vertical offset (the sum of heights for all objects above) a certain line number.
     *
     * @param lineNumber The line number
     * @return The sum of heights for all objects above `lineNumber`.
     */
    LinesLayout.prototype.getVerticalOffsetForLineNumber = function (lineNumber) {
        lineNumber = lineNumber | 0;
        var previousLinesHeight;
        if (lineNumber > 1) {
            previousLinesHeight = this._lineHeight * (lineNumber - 1);
        }
        else {
            previousLinesHeight = 0;
        }
        var previousWhitespacesHeight = this._whitespaces.getAccumulatedHeightBeforeLineNumber(lineNumber);
        return previousLinesHeight + previousWhitespacesHeight;
    };
    /**
     * Returns the accumulated height of whitespaces before the given line number.
     *
     * @param lineNumber The line number
     */
    LinesLayout.prototype.getWhitespaceAccumulatedHeightBeforeLineNumber = function (lineNumber) {
        return this._whitespaces.getAccumulatedHeightBeforeLineNumber(lineNumber);
    };
    LinesLayout.prototype.getWhitespaceMinWidth = function () {
        return this._whitespaces.getMinWidth();
    };
    /**
     * Check if `verticalOffset` is below all lines.
     */
    LinesLayout.prototype.isAfterLines = function (verticalOffset) {
        var totalHeight = this.getLinesTotalHeight();
        return verticalOffset > totalHeight;
    };
    /**
     * Find the first line number that is at or after vertical offset `verticalOffset`.
     * i.e. if getVerticalOffsetForLine(line) is x and getVerticalOffsetForLine(line + 1) is y, then
     * getLineNumberAtOrAfterVerticalOffset(i) = line, x <= i < y.
     *
     * @param verticalOffset The vertical offset to search at.
     * @return The line number at or after vertical offset `verticalOffset`.
     */
    LinesLayout.prototype.getLineNumberAtOrAfterVerticalOffset = function (verticalOffset) {
        verticalOffset = verticalOffset | 0;
        if (verticalOffset < 0) {
            return 1;
        }
        var linesCount = this._lineCount | 0;
        var lineHeight = this._lineHeight;
        var minLineNumber = 1;
        var maxLineNumber = linesCount;
        while (minLineNumber < maxLineNumber) {
            var midLineNumber = ((minLineNumber + maxLineNumber) / 2) | 0;
            var midLineNumberVerticalOffset = this.getVerticalOffsetForLineNumber(midLineNumber) | 0;
            if (verticalOffset >= midLineNumberVerticalOffset + lineHeight) {
                // vertical offset is after mid line number
                minLineNumber = midLineNumber + 1;
            }
            else if (verticalOffset >= midLineNumberVerticalOffset) {
                // Hit
                return midLineNumber;
            }
            else {
                // vertical offset is before mid line number, but mid line number could still be what we're searching for
                maxLineNumber = midLineNumber;
            }
        }
        if (minLineNumber > linesCount) {
            return linesCount;
        }
        return minLineNumber;
    };
    /**
     * Get all the lines and their relative vertical offsets that are positioned between `verticalOffset1` and `verticalOffset2`.
     *
     * @param verticalOffset1 The beginning of the viewport.
     * @param verticalOffset2 The end of the viewport.
     * @return A structure describing the lines positioned between `verticalOffset1` and `verticalOffset2`.
     */
    LinesLayout.prototype.getLinesViewportData = function (verticalOffset1, verticalOffset2) {
        verticalOffset1 = verticalOffset1 | 0;
        verticalOffset2 = verticalOffset2 | 0;
        var lineHeight = this._lineHeight;
        // Find first line number
        // We don't live in a perfect world, so the line number might start before or after verticalOffset1
        var startLineNumber = this.getLineNumberAtOrAfterVerticalOffset(verticalOffset1) | 0;
        var startLineNumberVerticalOffset = this.getVerticalOffsetForLineNumber(startLineNumber) | 0;
        var endLineNumber = this._lineCount | 0;
        // Also keep track of what whitespace we've got
        var whitespaceIndex = this._whitespaces.getFirstWhitespaceIndexAfterLineNumber(startLineNumber) | 0;
        var whitespaceCount = this._whitespaces.getCount() | 0;
        var currentWhitespaceHeight;
        var currentWhitespaceAfterLineNumber;
        if (whitespaceIndex === -1) {
            whitespaceIndex = whitespaceCount;
            currentWhitespaceAfterLineNumber = endLineNumber + 1;
            currentWhitespaceHeight = 0;
        }
        else {
            currentWhitespaceAfterLineNumber = this._whitespaces.getAfterLineNumberForWhitespaceIndex(whitespaceIndex) | 0;
            currentWhitespaceHeight = this._whitespaces.getHeightForWhitespaceIndex(whitespaceIndex) | 0;
        }
        var currentVerticalOffset = startLineNumberVerticalOffset;
        var currentLineRelativeOffset = currentVerticalOffset;
        // IE (all versions) cannot handle units above about 1,533,908 px, so every 500k pixels bring numbers down
        var STEP_SIZE = 500000;
        var bigNumbersDelta = 0;
        if (startLineNumberVerticalOffset >= STEP_SIZE) {
            // Compute a delta that guarantees that lines are positioned at `lineHeight` increments
            bigNumbersDelta = Math.floor(startLineNumberVerticalOffset / STEP_SIZE) * STEP_SIZE;
            bigNumbersDelta = Math.floor(bigNumbersDelta / lineHeight) * lineHeight;
            currentLineRelativeOffset -= bigNumbersDelta;
        }
        var linesOffsets = [];
        var verticalCenter = verticalOffset1 + (verticalOffset2 - verticalOffset1) / 2;
        var centeredLineNumber = -1;
        // Figure out how far the lines go
        for (var lineNumber = startLineNumber; lineNumber <= endLineNumber; lineNumber++) {
            if (centeredLineNumber === -1) {
                var currentLineTop = currentVerticalOffset;
                var currentLineBottom = currentVerticalOffset + lineHeight;
                if ((currentLineTop <= verticalCenter && verticalCenter < currentLineBottom) || currentLineTop > verticalCenter) {
                    centeredLineNumber = lineNumber;
                }
            }
            // Count current line height in the vertical offsets
            currentVerticalOffset += lineHeight;
            linesOffsets[lineNumber - startLineNumber] = currentLineRelativeOffset;
            // Next line starts immediately after this one
            currentLineRelativeOffset += lineHeight;
            while (currentWhitespaceAfterLineNumber === lineNumber) {
                // Push down next line with the height of the current whitespace
                currentLineRelativeOffset += currentWhitespaceHeight;
                // Count current whitespace in the vertical offsets
                currentVerticalOffset += currentWhitespaceHeight;
                whitespaceIndex++;
                if (whitespaceIndex >= whitespaceCount) {
                    currentWhitespaceAfterLineNumber = endLineNumber + 1;
                }
                else {
                    currentWhitespaceAfterLineNumber = this._whitespaces.getAfterLineNumberForWhitespaceIndex(whitespaceIndex) | 0;
                    currentWhitespaceHeight = this._whitespaces.getHeightForWhitespaceIndex(whitespaceIndex) | 0;
                }
            }
            if (currentVerticalOffset >= verticalOffset2) {
                // We have covered the entire viewport area, time to stop
                endLineNumber = lineNumber;
                break;
            }
        }
        if (centeredLineNumber === -1) {
            centeredLineNumber = endLineNumber;
        }
        var endLineNumberVerticalOffset = this.getVerticalOffsetForLineNumber(endLineNumber) | 0;
        var completelyVisibleStartLineNumber = startLineNumber;
        var completelyVisibleEndLineNumber = endLineNumber;
        if (completelyVisibleStartLineNumber < completelyVisibleEndLineNumber) {
            if (startLineNumberVerticalOffset < verticalOffset1) {
                completelyVisibleStartLineNumber++;
            }
        }
        if (completelyVisibleStartLineNumber < completelyVisibleEndLineNumber) {
            if (endLineNumberVerticalOffset + lineHeight > verticalOffset2) {
                completelyVisibleEndLineNumber--;
            }
        }
        return {
            bigNumbersDelta: bigNumbersDelta,
            startLineNumber: startLineNumber,
            endLineNumber: endLineNumber,
            relativeVerticalOffset: linesOffsets,
            centeredLineNumber: centeredLineNumber,
            completelyVisibleStartLineNumber: completelyVisibleStartLineNumber,
            completelyVisibleEndLineNumber: completelyVisibleEndLineNumber
        };
    };
    LinesLayout.prototype.getVerticalOffsetForWhitespaceIndex = function (whitespaceIndex) {
        whitespaceIndex = whitespaceIndex | 0;
        var afterLineNumber = this._whitespaces.getAfterLineNumberForWhitespaceIndex(whitespaceIndex);
        var previousLinesHeight;
        if (afterLineNumber >= 1) {
            previousLinesHeight = this._lineHeight * afterLineNumber;
        }
        else {
            previousLinesHeight = 0;
        }
        var previousWhitespacesHeight;
        if (whitespaceIndex > 0) {
            previousWhitespacesHeight = this._whitespaces.getAccumulatedHeight(whitespaceIndex - 1);
        }
        else {
            previousWhitespacesHeight = 0;
        }
        return previousLinesHeight + previousWhitespacesHeight;
    };
    LinesLayout.prototype.getWhitespaceIndexAtOrAfterVerticallOffset = function (verticalOffset) {
        verticalOffset = verticalOffset | 0;
        var midWhitespaceIndex, minWhitespaceIndex = 0, maxWhitespaceIndex = this._whitespaces.getCount() - 1, midWhitespaceVerticalOffset, midWhitespaceHeight;
        if (maxWhitespaceIndex < 0) {
            return -1;
        }
        // Special case: nothing to be found
        var maxWhitespaceVerticalOffset = this.getVerticalOffsetForWhitespaceIndex(maxWhitespaceIndex);
        var maxWhitespaceHeight = this._whitespaces.getHeightForWhitespaceIndex(maxWhitespaceIndex);
        if (verticalOffset >= maxWhitespaceVerticalOffset + maxWhitespaceHeight) {
            return -1;
        }
        while (minWhitespaceIndex < maxWhitespaceIndex) {
            midWhitespaceIndex = Math.floor((minWhitespaceIndex + maxWhitespaceIndex) / 2);
            midWhitespaceVerticalOffset = this.getVerticalOffsetForWhitespaceIndex(midWhitespaceIndex);
            midWhitespaceHeight = this._whitespaces.getHeightForWhitespaceIndex(midWhitespaceIndex);
            if (verticalOffset >= midWhitespaceVerticalOffset + midWhitespaceHeight) {
                // vertical offset is after whitespace
                minWhitespaceIndex = midWhitespaceIndex + 1;
            }
            else if (verticalOffset >= midWhitespaceVerticalOffset) {
                // Hit
                return midWhitespaceIndex;
            }
            else {
                // vertical offset is before whitespace, but midWhitespaceIndex might still be what we're searching for
                maxWhitespaceIndex = midWhitespaceIndex;
            }
        }
        return minWhitespaceIndex;
    };
    /**
     * Get exactly the whitespace that is layouted at `verticalOffset`.
     *
     * @param verticalOffset The vertical offset.
     * @return Precisely the whitespace that is layouted at `verticaloffset` or null.
     */
    LinesLayout.prototype.getWhitespaceAtVerticalOffset = function (verticalOffset) {
        verticalOffset = verticalOffset | 0;
        var candidateIndex = this.getWhitespaceIndexAtOrAfterVerticallOffset(verticalOffset);
        if (candidateIndex < 0) {
            return null;
        }
        if (candidateIndex >= this._whitespaces.getCount()) {
            return null;
        }
        var candidateTop = this.getVerticalOffsetForWhitespaceIndex(candidateIndex);
        if (candidateTop > verticalOffset) {
            return null;
        }
        var candidateHeight = this._whitespaces.getHeightForWhitespaceIndex(candidateIndex);
        var candidateId = this._whitespaces.getIdForWhitespaceIndex(candidateIndex);
        var candidateAfterLineNumber = this._whitespaces.getAfterLineNumberForWhitespaceIndex(candidateIndex);
        return {
            id: candidateId,
            afterLineNumber: candidateAfterLineNumber,
            verticalOffset: candidateTop,
            height: candidateHeight
        };
    };
    /**
     * Get a list of whitespaces that are positioned between `verticalOffset1` and `verticalOffset2`.
     *
     * @param verticalOffset1 The beginning of the viewport.
     * @param verticalOffset2 The end of the viewport.
     * @return An array with all the whitespaces in the viewport. If no whitespace is in viewport, the array is empty.
     */
    LinesLayout.prototype.getWhitespaceViewportData = function (verticalOffset1, verticalOffset2) {
        verticalOffset1 = verticalOffset1 | 0;
        verticalOffset2 = verticalOffset2 | 0;
        var startIndex = this.getWhitespaceIndexAtOrAfterVerticallOffset(verticalOffset1);
        var endIndex = this._whitespaces.getCount() - 1;
        if (startIndex < 0) {
            return [];
        }
        var result = [];
        for (var i = startIndex; i <= endIndex; i++) {
            var top_1 = this.getVerticalOffsetForWhitespaceIndex(i);
            var height = this._whitespaces.getHeightForWhitespaceIndex(i);
            if (top_1 >= verticalOffset2) {
                break;
            }
            result.push({
                id: this._whitespaces.getIdForWhitespaceIndex(i),
                afterLineNumber: this._whitespaces.getAfterLineNumberForWhitespaceIndex(i),
                verticalOffset: top_1,
                height: height
            });
        }
        return result;
    };
    /**
     * Get all whitespaces.
     */
    LinesLayout.prototype.getWhitespaces = function () {
        return this._whitespaces.getWhitespaces(this._lineHeight);
    };
    return LinesLayout;
}());
export { LinesLayout };
