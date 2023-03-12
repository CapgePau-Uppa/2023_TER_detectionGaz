import React from 'react';
import MetricCard from '../MetricCard';
import {
  ParameterCard,
  ParameterHeader,
  ParameterHeaderText,
  MetricView,
} from './styles';
import SetParameter from 'src/interfaces/SetParameter';

const DetailedAlarmMetricDisplay = ({ metric }: { metric: SetParameter }) => {
  const { name, value, lowerLimit, upperLimit } = metric;
  const state = getStateFromValue(value);

  function getStateFromValue(valueForState: number) {
    if (valueForState < lowerLimit || valueForState > upperLimit) {
      return 'alarm';
    }
    return 'normal';
  }

  return (
    <ParameterCard>
      <ParameterHeader>
        <ParameterHeaderText>{name}</ParameterHeaderText>
      </ParameterHeader>
      <MetricView>
        <MetricCard state={state} metric={metric} />
      </MetricView>
    </ParameterCard>
  );
};
export default DetailedAlarmMetricDisplay;
