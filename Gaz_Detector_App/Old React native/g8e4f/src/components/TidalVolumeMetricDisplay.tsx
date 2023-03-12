import * as React from 'react';
import MetricDisplay from './MetricDisplay';

export default function TidalVolumeMetricDisplay(props: any) {
  const { ventilationMode, parameter } = props;

  function isPressureControlledVentilationMode(): boolean {
    return ventilationMode === 'PCV' || ventilationMode === 'AC-PCV';
  }

  return (
    <MetricDisplay
      title={parameter.name}
      value={isPressureControlledVentilationMode() ? null : parameter.setValue}
      unit={parameter.unit}
    />
  );
}
