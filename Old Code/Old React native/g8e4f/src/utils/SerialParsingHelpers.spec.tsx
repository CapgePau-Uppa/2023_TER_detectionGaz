import { getAlarmValues } from './SerialParsingHelpers';

jest.useFakeTimers();

test('check System Reset value', () => {
  let testPacket: number[] = new Array<number>(49).fill(0);
  testPacket[41] = 64;
  const alarms: string[] = getAlarmValues(testPacket);
  expect(alarms.length).toBe(1);
  expect(alarms[0]).toBe(
    'System Reset - Please Recheck FiO2/PEEP/Minute Ventilation Settings',
  );
});

afterAll(() => {
  jest.runAllTimers();
});
