<script>
import { Pie } from "vue-chartjs";

export default {
  extends: Pie,
  options: {
    maintainAspectRatio: false,
  },
  props: ["chartData"],
  watch: {
    chartData: function () {
      this.renderChart(this.chartData, {
        maintainAspectRatio: false,
      });
    },
  },
  mounted() {
    this.renderChart(this.chartData, {
      maintainAspectRatio: false,
    });
  },
};
</script>
