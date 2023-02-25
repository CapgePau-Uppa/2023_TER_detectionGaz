import { useReading } from '../logic/useReading';
import { useEffect } from 'react';
import AlarmsManager from '../logic/AlarmsManager';

export default function AlarmsBanner() {
  const reading = useReading();
  const readingValues = reading.values;

  useEffect(() => {
    AlarmsManager.onNewReading(readingValues);
  }, [readingValues]);

  return null;
}
