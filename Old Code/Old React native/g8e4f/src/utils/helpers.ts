export function convertArrayToMatrix<T>(
  arrayToConvert: Array<T>,
  itemsPerRow: number,
): Array<Array<T>> {
  const matrix: Array<Array<T>> = [];

  for (
    let itemIndex = 0, rowIndex = -1;
    itemIndex < arrayToConvert.length;
    itemIndex++
  ) {
    if (itemIndex % itemsPerRow === 0) {
      rowIndex++;
      matrix[rowIndex] = [];
    }
    matrix[rowIndex].push(arrayToConvert[itemIndex]);
  }
  return matrix;
}
