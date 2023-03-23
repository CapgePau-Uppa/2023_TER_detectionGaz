# ventilator-app

![Test](https://github.com/OpenVentPk/ventilator-app/workflows/Test/badge.svg?branch=master&event=push)

Application written in React Native to allow connecting to a ventilator via cable and show information about the ventilation. Our current focus is only on the Android version for now.

## Features

### Monitoring

View the Tidal Volume, Pressure and Flow rate graphs as well as other essential information such as FiO2, Patient Respiratory Rate, Ventilation Mode, Peep etc.

![monitoring-screen](./img/monitoring-screen.png)

The graphs also show indicators to denote the start of a breath

![breath-indicator](./img/breath-indicator.png)

### Alarms

View all the possible alarms that can occur as well as the ones which are active currently.

![alarms-screen](./img/alarms-screen.png)

Regardless of where you are on the screen, if there are any active alarms, they will be shown via a notification banner.

![alarms-banner](./img/alarms-banner.png)

## Contributing

Before submitting a pull request, please take a moment to look over the [contributing guidelines](./CONTRIBUTING.md) first.

## License

The ventilator app is available under the [BSD-3-Clause License](./LICENSE).
