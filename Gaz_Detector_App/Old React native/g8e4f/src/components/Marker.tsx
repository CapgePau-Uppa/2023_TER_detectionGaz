import * as React from 'react';
import { Polygon } from 'react-native-svg';

// TODO: Use interface for props rather than destructuring here
export default function Markers({ x, y, markerIndices, markerHeight }: any) {
  if (markerIndices === undefined) {
    markerIndices = [];
  }

  return markerIndices.map((markerIndex: number) => (
    <Polygon
      key={markerIndex}
      points={[
        `${x(markerIndex)}, ${y(-5)}`,
        `${x(markerIndex - 5)}, ${y(-5 - markerHeight)}`,
        `${x(markerIndex + 5)}, ${y(-5 - markerHeight)}`,
      ]}
      stroke="white"
      fill="white"
    />
  ));
}
